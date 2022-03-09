import os
import shutil
import uvicorn
import face_recognition
from typing import List
from gdrive_wrapper import gdrive
from asgiref.sync import sync_to_async
from psql_database import Database
from fastapi import FastAPI, File, UploadFile, Form


db_host = 'localhost' #'us-cdbr-iron-east-01.cleardb.net'
db_user =  'postgres' #'be6a5ab891fb44'
db_psswrd = '3112003' #heroku-psswrd
db_name = 'sih_attendance' #heroku-db

# db_host = 'ec2-52-207-74-100.compute-1.amazonaws.com' 
# db_user =  'sxxkdscneuzrwf'
# db_psswrd = '0e4072748413d89453bc01d7eb6d8b5d9c128f0c4ce4550defbb3b4d4e203a7f'
# db_name = 'd3rhldildqlaje'

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)
mydrive = gdrive()

app = FastAPI()

def start_verify(user_id, format):
    mydrive.download_user_img(user_id)
    match = False
    for each_img in os.listdir('img_db/'+user_id):

        known_image = face_recognition.load_image_file(f'img_db/{user_id}/{each_img}')
        unknown_image = face_recognition.load_image_file(f'status/{user_id}/img.{format}')

        know_encoding = face_recognition.face_encodings(known_image)[0]
        unknown_encoding = face_recognition.face_encodings(unknown_image)[0]
        results = face_recognition.compare_faces([know_encoding], unknown_encoding)

        if results[0] == True:
            match = True
            break

    if match:
        os.mkdir(f"status/{user_id}/VERIFIED")

    else :
        os.mkdir(f"status/{user_id}/UNVERIFIED")

    clear_local_data(user_id)

def clear_local_data(user_id):
    for img in os.listdir(f"img_db/{user_id}"):
        os.remove(f"img_db/{user_id}/{img}")

    os.rmdir(f"img_db/{user_id}")


@app.post('/verify')
async def verify(
    image: UploadFile = File(...),
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    img = image
    
    if not mydb.check_user_id_exist(user_id): # occurs only while testing or spamming
        return "NOPE"

    if user_id not in os.listdir(f"status"):
        os.mkdir(f"status/{user_id}")
    

    format = str(img.filename).split('.')[-1].split("'")[0]
    file_location = f"status/{user_id}/img.{format}"
    with open(file_location, "wb+") as buffer:
        shutil.copyfileobj(img.file, buffer)

    response = await sync_to_async(start_verify)(user_id, format)
    return "WAIT"


# @app.post('/check_in')
# def update_log(
#     user_id : str = Form(...),
#     date_time: str = Form(...)
# ):
#     user_id = user_id[1:-1]
#     mydb.check_in(user_id, date_time)

#     return "CHECKED IN"


# @app.post('/check_out')
# def check_out(
#     user_id : str = Form(...),
#     date_time: str = Form(...)
# ):
#     user_id = user_id[1:-1]
#     mydb.check_out(user_id, date_time)

#     return "CHECKED OUT"

@app.post('/update_log')
def update_log(
    user_id : str = Form(...),
    date_time: str = Form(...)
):
    user_id = user_id[1:-1]
    mydb.update_log(user_id, date_time)

    return "LOG UPDATED"


@app.post('/status')
async def status(
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    if user_id not in os.listdir("status"):
        return "NOT IN PATH"
        
    directories = os.listdir(f"status/{user_id}")

    if len(directories) == 2:
        
        if "VERIFIED" in directories :
            os.rmdir(f"status/{user_id}/VERIFIED")
            directories.remove('VERIFIED')
            os.remove(f"status/{user_id}/{directories[0]}")
            os.rmdir(f"status/{user_id}")
            return "VERIFIED"

        os.rmdir(f"status/{user_id}/UNVERIFIED")
        directories.remove('UNVERIFIED')
        os.remove(f"status/{user_id}/{directories[0]}")
        os.rmdir(f"status/{user_id}")

        return "UNVERIFIED"
    else:
        return "WAIT"        
            

@app.post('/signup')
async def signup(
    mail_id: str = Form(...),
    user_name: str = Form(...),
    password: str = Form(...),
    name: str = Form(...),
    age: str = Form(...),
    address: str = Form(...),
    contact_no: str = Form(...),
    blood_grp: str = Form(...),
    files: List[UploadFile] = File(...)
):

    data = [mail_id, user_name, password, name, age, address, contact_no, blood_grp]

    user_name_availablity, user_id = mydb.sign_up(tuple(data))
    if user_id is None:
        return "ALREADY IN USE"

    os.mkdir(f"img_db/{user_id}")
    for each_image in files:
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
    return unique_id



@app.post('/get_info')
async def get_info(
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    if not mydb.check_user_id_exist(user_id):
        return "NOPE"

    data_args = 'name,age,address,contact_no,blood_grp'.split(',')
    data = mydb.get_user_details(user_id)
    
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