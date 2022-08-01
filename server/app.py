import os
import io
import shutil
import time
import base64
import uvicorn
# from gdrive_wrapper import gdrive
from asgiref.sync import sync_to_async
# from PIL import Image
from psql_database import Database
from fastapi import FastAPI, File, UploadFile, Form
from fastapi.responses import StreamingResponse, Response


db_host = 'localhost' #'us-cdbr-iron-east-01.cleardb.net'
db_user =  'postgres' #'be6a5ab891fb44'
db_psswrd = '3112003' #heroku-psswrd
db_name = 'sih_attendance' #heroku-db

# db_host = 'ec2-52-207-74-100.compute-1.amazonaws.com' 
# db_user =  'sxxkdscneuzrwf'
# db_psswrd = '0e4072748413d89453bc01d7eb6d8b5d9c128f0c4ce4550defbb3b4d4e203a7f'
# db_name = 'd3rhldildqlaje'

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)
# mydrive = gdrive()

app = FastAPI()

def clear_local_data(user_id):
    for img in os.listdir(f"img_db/{user_id}"):
        os.remove(f"img_db/{user_id}/{img}")

    os.rmdir(f"img_db/{user_id}")


@app.post('/update_log')
def update_log(
    user_id : str = Form(...),
    check_in: str = Form(...),
    check_out: str = Form(...)
):
    user_id = user_id[1:-1]

    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    if check_in == "blah-null":
        check_in = None

    if check_out == "blah-null":
        check_out = None

    mydb.update_log(user_id, check_in, check_out)

    return "LOG UPDATED"

@app.post('/check_in_out_status')
def check_in_out_status(
    user_id : str = Form(...),
):
    user_id = user_id[1:-1]

    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    return mydb.check_in_out(user_id)

@app.post('/signup')
async def signup(
    mail_id: str = Form(...),
    user_name: str = Form(...),
    password: str = Form(...),
    name: str = Form(...),
    designation: str = Form(...),
    emp_no: str = Form(...),
    gender: str = Form(...),
    office_address: str = Form(...),
    contact_no: str = Form(...),
    embed1 : list = Form(...),
    embed2 : list = Form(...),
    embed3 : list = Form(...),
    files: UploadFile = File(...)
):
    data = [mail_id, user_name, password, name, designation, emp_no, gender, office_address, contact_no, embed1, embed2, embed3]

    user_name_availablity, user_id = mydb.sign_up(tuple(data))
    if user_id is None:
        return "ALREADY IN USE"

    os.mkdir(f"img_db/{user_id}")
    
    each_image = files
    img = each_image.filename
    file_location = f"img_db/{user_id}/{img}.png"

    with open(file_location, "wb+") as buffer:
        shutil.copyfileobj(each_image.file, buffer)

    # pil_img = Image.open(f"img_db/{user_id}/{img}.png")
    # pil_img.save(f"img_db/{user_id}/{img}_optimized.png", optimize=True)

    # if os.path.getsize(f"img_db/{user_id}/{img}_optimized.png")>os.path.getsize(f"img_db/{user_id}/{img}.png"):
    #     os.remove(f"img_db/{user_id}/{img}_optimized.png")
    # else:
    #     os.remove(f"img_db/{user_id}/{img}.png")
    #     os.rename(f"img_db/{user_id}/{img}_optimized.png", f"img_db/{user_id}/{img}.png")


    start = time.time()
    mydb.upload_img(user_id, base64.b64encode(open(f"img_db/{user_id}/{img}.png",'rb').read()))
    clear_local_data(user_id)

    print(start-time.time(), flush=True)

    return user_id


@app.post('/login')
async def login(
    user_name_or_mail_id: str = Form(...),
    type_of_login: str = Form(...),
    password: str = Form(...)
):
    data = [user_name_or_mail_id, password]

    unique_id = mydb.user_login_details(data, type_of_login = type_of_login)
    return str(unique_id)

@app.post('/check_username')
async def check_username(
    username: str = Form(...),
):
    data = [username]

    if mydb.check_username(data):
        return "YES"

    return "NO"


@app.post('/get_info')
async def get_info(
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    data_args = 'name,designation,emp_no,gender,office_address,contact_no,check_in,check_out'.split(',')
    data = mydb.get_user_details(user_id)
    
    form = {}
    for i, j in zip(data_args, data):
        form[i] = j
        
    return form

@app.post('/get_embed')
async def get_embed(
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    data_args = 'embed1,embed2,embed3'.split(',')
    data = mydb.get_embeds(user_id)
    
    form = {}
    for i, j in zip(data_args, data):
        j = j[0][1:-1].split(', ')
        j = list(map(float,j))
        form[i] = j
        
    return form


# @app.post('/gdrive_refresh')
# def gdrive_refresh():
#     mydrive.refresh()
#     return "FINISHED REFRESHING"


@app.post('/get_branch_info')
async def get_branch_info(
    user_id: str = Form(...),
    branch_name: str = Form(...)
):
    user_id = user_id[1:-1]
    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    return mydb.get_branch_info(branch_name)

@app.post('/get_img')
async def get_img(
    user_id: str = Form(...),
):
    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    # from PIL import Image
    # image = Image.open()
    # image.show()
    
    return Response(content=base64.b64decode(mydb.get_img(user_id)), media_type="image/png")

if __name__ == '__main__':
    uvicorn.run(app, port=5000)