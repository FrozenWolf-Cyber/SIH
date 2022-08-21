import os
import shutil
import base64
import asyncio
import uvicorn
import logging
import pickle
from encryption import encryption_algo
from psql_database import Database
from fastapi import FastAPI, File, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import Response, JSONResponse

db_host = 'localhost' #'us-cdbr-iron-east-01.cleardb.net'
db_user =  'postgres' #'be6a5ab891fb44'
db_psswrd = '3112003' #heroku-psswrd
db_name = 'sih_attendance' #heroku-db

# db_host = 'ec2-52-207-74-100.compute-1.amazonaws.com' 
# db_user =  'sxxkdscneuzrwf'
# db_psswrd = '0e4072748413d89453bc01d7eb6d8b5d9c128f0c4ce4550defbb3b4d4e203a7f'
# db_name = 'd3rhldildqlaje'

ADMIN_USERNAME = 'ADMIN'
ADMIN_PSSWRD = 'ADMIN'

encryptor = encryption_algo('cervh0s3e2hnpaitaeitad0sn', 'eaia0dnesp3thach2tir0esnv')
# encryptor = pickle.load(open('encryptor.pkl', 'rb'))

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)


app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

async def clear_local_data(emp_no):
    for img in os.listdir(f"img_db/{emp_no}"):
        os.remove(f"img_db/{emp_no}/{img}")

    os.rmdir(f"img_db/{emp_no}")


async def write_img_data(emp_no, each_image):
    img = each_image.filename
    file_location = f"img_db/{emp_no}/{img}.jpg"

    with open(file_location, "wb+") as buffer:
        shutil.copyfileobj(each_image.file, buffer)


async def exception_handle(msg, func, *args):
    try:
        return await func(*args)

    except:
        logger = logging.getLogger()
        logging.exception(msg)
        logger.handlers[0].flush()

        return msg


@app.post('/update_log')
async def update_log(
    emp_no : str = Form(...),
    check_in: str = Form(...),
    check_out: str = Form(...),
    latitude: str= Form(...),
    longitude: str= Form(...)
):
    emp_no = emp_no[1:-1]

    if not await mydb.check_emp_no_signed(emp_no):
        return "NOPE"

    if check_in == "blah-null":
        check_in = None

    if check_out == "blah-null":
        check_out = None

    return await exception_handle("SERVER ERROR WHILE UPDATING LOG", mydb.update_log, emp_no, check_in, check_out, latitude, longitude)


@app.post('/modify_log')
async def modify_log(
    emp_no : str = Form(...),
    old_check_in: str = Form(...),
    old_check_out: str = Form(...),
    new_check_in: str = Form(...),
    new_check_out: str = Form(...)
):
    emp_no = emp_no[1:-1]

    if not await mydb.check_emp_no_signed(emp_no):
        return "NOPE"

    return await exception_handle("SERVER ERROR WHILE MODIFYING LOG", mydb.modify_log, emp_no, old_check_in, old_check_out, new_check_in, new_check_out)


@app.post('/check_in_out_status')
async def check_in_out_status(
    emp_no : str = Form(...),
):
    emp_no = emp_no[1:-1]

    if not await mydb.check_emp_no_signed(emp_no):
        return "NOPE"
    
    return await exception_handle("SERVER ERROR WHILE UPDATING LOG", mydb.check_in_out_status, emp_no)


@app.post('/admin_signup')
async def admin_signup(
    mail_id: str = Form(...),
    name: str = Form(...),
    designation: str = Form(...),
    gender: str = Form(...),
    branch_name: str = Form(...),
    contact_no: str = Form(...),

):

    mail_id, name, designation, gender, contact_no = encryptor.AES_encrypt(mail_id), encryptor.AES_encrypt(name), encryptor.AES_encrypt(designation), encryptor.AES_encrypt(gender), encryptor.AES_encrypt(contact_no)

    data = [mail_id, name, designation, gender, branch_name, contact_no]

    sucess = False
    tries = 0
    while not sucess:
        try:
            e = await mydb.admin_signup(data)
            
        except:
            # print("SERVER ERROR WHILE UPDATING ADMIN SIGNUP IN PSQL")
            await asyncio.sleep(0.5)

        else:
            sucess = True

        if tries>=50:
            return "SERVER IS BUSY TRY SOMETIME LATER"

        tries+=1




    if len(e)==2:
        user_name_availablity, emp_no = e

    else:
        return e

    if emp_no is None:
        return "ALREADY IN USE"

    return emp_no


@app.post('/signup')
async def signup(
    user_name: str = Form(...),
    password: str = Form(...),
    emp_no: str = Form(...),
    embed1 : list = Form(...),
    embed2 : list = Form(...),
    embed3 : list = Form(...),
    files: UploadFile = File(...)
):

    emp_no = emp_no[1:-1]
    
    password = encryptor.SHA256_encrypt(password)
    # user_name = encryptor.SHA256_encrypt(user_name)


    data = [user_name, password, emp_no, embed1, embed2, embed3]

    if not await mydb.check_emp_no_exist_master(emp_no):
        return "EMPLOYEE NUMBER DOESN'T EXIST IN MASTER"

    e = await exception_handle("SERVER ERROR WHILE UPDATING SIGNUP IN PSQL", mydb.signup, tuple(data))

    if len(e)==2:
        user_name_availablity, emp_no = e

    else:
        return e

    if emp_no is None:
        return "ALREADY IN USE"

    os.mkdir(f"img_db/{emp_no}")
    
    each_image = files

    e = await exception_handle("SERVER ERROR WHILE PROCESSING IMAGE DATA", write_img_data, emp_no, each_image)

    if e is not None:
        await clear_local_data(emp_no)
        return e

    e = await exception_handle("SERVER ERROR WHILE UPLOADING IMAGE DATA TO PSQL", mydb.upload_img, emp_no, base64.b64encode(open(f"img_db/{emp_no}/{each_image.filename}.jpg",'rb').read()))

    if e is not None:
        return e

    await clear_local_data(emp_no)
    return emp_no



@app.post('/login')
async def login(
    user_name_or_mail_id: str = Form(...),
    type_of_login: str = Form(...),
    password: str = Form(...)
):

    if user_name_or_mail_id == ADMIN_USERNAME and password == ADMIN_PSSWRD:
        return "ADMIN"

    password = encryptor.SHA256_encrypt(password)
    # user_name_or_mail_id = encryptor.SHA256_encrypt(user_name_or_mail_id)
        
    data = [user_name_or_mail_id, password]

    return str(await exception_handle("SERVER ERROR WHILE CHECKING LOGIN DETAILS", mydb.user_login_details, data, type_of_login))

@app.post('/check_username')
async def check_username(
    username: str = Form(...),
):

    # username = encryptor.SHA256_encrypt(username)
    if await mydb.check_username(username):
        return "YES"

    return "NO"


@app.post('/check_emp_no')
async def check_emp_no(
    emp_no: str = Form(...),
):

    result = []
    emp_no = emp_no[1:-1]
    if await mydb.check_emp_no_exist_master(emp_no):
        result.append("YES")

    else:
        result.append("NO")

    if await mydb.check_emp_no_signed(emp_no):
            result.append("YES")

    else:
        result.append("NO")   

    return result
    
    
@app.post('/get_info')
async def get_info(
    emp_no: str = Form(...)
):
    emp_no = emp_no[1:-1]
    if not await mydb.check_emp_no_signed(emp_no):
        return "EMPLOYEE NUMBER DOESN'T EXIST"

    data_args = 'name,designation,emp_no,gender,branch_name,contact_no,check_in,check_out,in_latitude,in_longitude,out_latitude,out_longitude'.split(',')
    decrypt_for = 'name,designation,gender,branch_name,contact_no'.split(',')
    e = await exception_handle("SERVER ERROR WHILE RETRIEVING USER INFO FROM PSQL", mydb.get_user_details, emp_no)
    if e == "SERVER ERROR WHILE RETRIEVING USER INFO FROM PSQL":
        return e
    else:
        data = e
    
    form = {}
    for i, j in zip(data_args, data):
        if i in decrypt_for:
            j = encryptor.AES_decrypt(j)
        form[i] = j
        
    return form

@app.post('/get_embed')
async def get_embed(
    emp_no: str = Form(...)
):
    emp_no = emp_no[1:-1]
    if not await mydb.check_emp_no_signed(emp_no):
        return "EMPLOYEE NUMBER DOESN'T EXIST"

    data_args = 'embed1,embed2,embed3'.split(',')
    
    e = await exception_handle("SERVER ERROR WHILE RETRIEVING EMBEDDINGS FROM PSQL", mydb.get_embeds, emp_no)
    if e == "SERVER ERROR WHILE RETRIEVING EMBEDDINGS FROM PSQL":
        return e
    else:
        data = e

    # print(len(data), flush=True)
    # print(len(data), data, data[0], sep="\n\n\n", flush=True)
    form = {}
    for i, j in zip(data_args, data):
        # print ('j', j, j[0])
        j = eval(','.join(j))
        form[i] = j
        
    return form


@app.post('/get_branch_info')
async def get_branch_info(
    emp_no: str = Form(...),
    branch_name: str = Form(...)
):
    emp_no = emp_no[1:-1]
    if not await mydb.check_emp_no_signed(emp_no):
        return "EMPLOYEE NUMBER DOESN'T EXIST"

    return await exception_handle("SERVER ERROR WHILE GETTING OFFICE ADDRESS FROM PSQL", mydb.get_branch_info, branch_name)

@app.post('/get_img')
async def get_img(
    emp_no: str = Form(...),
):
    emp_no = emp_no[1:-1]
    if not await mydb.check_emp_no_signed(emp_no):
        return "EMPLOYEE NUMBER DOESN'T EXIST"

    e = await exception_handle("SERVER ERROR WHILE RETRIEVING IMAGE FROM SERVER", mydb.get_img, emp_no)
    if e == "SERVER ERROR WHILE RETRIEVING IMAGE FROM SERVER":
        return e

    decode_img = lambda y: Response(content=base64.b64decode(y), media_type="image/jpg")
    return decode_img(e)


# HANDLING WEBSITE REQUESTS
@app.post('/get_log_data')
async def get_log_data(
    last_n_days: int = Form(...),
):

    return await exception_handle("SERVER ERROR WHILE RETRIEVING LOG DATA FROM PSQL", mydb.get_log_data, last_n_days)


@app.post('/get_user_overview')
async def get_user_overview():

    data = await exception_handle("SERVER ERROR WHILE RETRIEVING USER OVERVIEW DATA FROM PSQL", mydb.get_user_overview)

    for k in data.keys():
        if k in ['name', 'designation', 'gender']:
            for i in range(len(data[k])):
                data[k][i] = encryptor.AES_decrypt(data[k][i])

    return data

@app.exception_handler(Exception)
async def validation_exception_handler(request, err):
    base_error_message = f"Failed to execute: {request.method}: {request.url}"
    # Change here to LOGGER
    return JSONResponse(status_code=400, content={"message": f"{base_error_message}. Detail: {err}"})

@app.on_event("startup")
async def startup():
    await mydb.database.connect()
    await mydb.create(encryptor)

@app.on_event("shutdown")
async def shutdown():
    await mydb.database.disconnect()

if __name__ == '__main__':
    uvicorn.run(app, port=5000)
