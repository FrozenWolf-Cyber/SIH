import os
import shutil
import uvicorn
from typing import List
from gdrive_wrapper import gdrive
from asgiref.sync import sync_to_async
from psql_database import Database
from fastapi import FastAPI, File, UploadFile, Form


# db_host = 'localhost' #'us-cdbr-iron-east-01.cleardb.net'
# db_user =  'postgres' #'be6a5ab891fb44'
# db_psswrd = 'aadarsh2003' #heroku-psswrd
# db_name = 'sih_attendance' #heroku-db

db_host = 'ec2-52-207-74-100.compute-1.amazonaws.com' 
db_user =  'sxxkdscneuzrwf'
db_psswrd = '0e4072748413d89453bc01d7eb6d8b5d9c128f0c4ce4550defbb3b4d4e203a7f'
db_name = 'd3rhldildqlaje'

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)
mydrive = gdrive()

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

    if check_in == "blah-null":
        check_in = ""

    if check_out == "blah-null":
        check_out = ""

    mydb.update_log(user_id, check_in, check_out)

    return "LOG UPDATED"


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
    file_location = f"img_db/{user_id}/{img}"
    with open(file_location, "wb+") as buffer:
        shutil.copyfileobj(each_image.file, buffer)

    mydrive.upload_img_folder(user_id)
    clear_local_data(user_id)

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

    data_args = 'name,designation,emp_no,gender,office_address,contact_no,log'.split(',')
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
        form[i] = j
        
    return form



@app.post('/master_cmd')
def master_cmd(
    cmd: str = Form(...)
):
    output = mydb.master_command(cmd)
    return output


@app.post('/gdrive_refresh')
def gdrive_refresh():
    mydrive.refresh()
    return "FINISHED REFRESHING"


if __name__ == '__main__':
    uvicorn.run(app, port=5000)