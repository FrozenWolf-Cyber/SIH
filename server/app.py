import os
import shutil
import base64
import uvicorn
import logging
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

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name)

# mydrive = gdrive()

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

async def clear_local_data(user_id):
    for img in os.listdir(f"img_db/{user_id}"):
        os.remove(f"img_db/{user_id}/{img}")

    os.rmdir(f"img_db/{user_id}")


async def write_img_data(user_id, each_image):
    img = each_image.filename
    file_location = f"img_db/{user_id}/{img}.jpg"

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
    user_id : str = Form(...),
    check_in: str = Form(...),
    check_out: str = Form(...)
):
    user_id = user_id[1:-1]

    if not await mydb.check_user_id_exist(user_id):
        return "NOPE"

    if check_in == "blah-null":
        check_in = None

    if check_out == "blah-null":
        check_out = None

    return await exception_handle("SERVER ERROR WHILE UPDATING LOG", mydb.update_log, user_id, check_in, check_out)


@app.post('/check_in_out_status')
async def check_in_out_status(
    user_id : str = Form(...),
):
    user_id = user_id[1:-1]

    if not await mydb.check_user_id_exist(user_id):
        return "NOPE"
    
    return await exception_handle("SERVER ERROR WHILE UPDATING LOG", mydb.check_in_out, user_id)


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

    e = await exception_handle("SERVER ERROR WHILE UPDATING SIGNUP IN PSQL", mydb.sign_up, tuple(data))

    if len(e)==2:
        user_name_availablity, user_id = e

    else:
        return e

    if user_id is None:
        return "ALREADY IN USE"

    os.mkdir(f"img_db/{user_id}")
    
    each_image = files

    e = await exception_handle("SERVER ERROR WHILE PROCESSING IMAGE DATA", write_img_data, user_id, each_image)

    if e is not None:
        await clear_local_data(user_id)
        return e

    e = await exception_handle("SERVER ERROR WHILE UPLOADING IMAGE DATA TO PSQL", mydb.upload_img, user_id, base64.b64encode(open(f"img_db/{user_id}/{each_image.filename}.jpg",'rb').read()))

    if e is not None:
        return e

    await clear_local_data(user_id)
    return user_id


@app.post('/login')
async def login(
    user_name_or_mail_id: str = Form(...),
    type_of_login: str = Form(...),
    password: str = Form(...)
):
    data = [user_name_or_mail_id, password]

    return str(await exception_handle("SERVER ERROR WHILE CHECKING LOGIN DETAILS", mydb.user_login_details, data, type_of_login))

@app.post('/check_username')
async def check_username(
    username: str = Form(...),
):

    if await mydb.check_username(username):
        return "YES"

    return "NO"


@app.post('/get_info')
async def get_info(
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    if not await mydb.check_user_id_exist(user_id):
        return "USERID DOESN'T EXIST"

    data_args = 'name,designation,emp_no,gender,office_address,contact_no,check_in,check_out'.split(',')
    e = await exception_handle("SERVER ERROR WHILE RETRIEVING USER INFO FROM PSQL", mydb.get_user_details, user_id)
    if e == "SERVER ERROR WHILE RETRIEVING USER INFO FROM PSQL":
        return e
    else:
        data = e
    
    form = {}
    for i, j in zip(data_args, data):
        form[i] = j
        
    return form

@app.post('/get_embed')
async def get_embed(
    user_id: str = Form(...)
):
    user_id = user_id[1:-1]
    if not await mydb.check_user_id_exist(user_id):
        return "USERID DOESN'T EXIST"

    data_args = 'embed1,embed2,embed3'.split(',')
    
    e = await exception_handle("SERVER ERROR WHILE RETRIEVING EMBEDDINGS FROM PSQL", mydb.get_embeds, user_id)
    if e == "SERVER ERROR WHILE RETRIEVING EMBEDDINGS FROM PSQL":
        return e
    else:
        data = e

    # print(len(data), flush=True)
    form = {}
    for i, j in zip(data_args, data):
        j = list(map(float,j[0]))
        form[i] = j
        
    return form


@app.post('/get_branch_info')
async def get_branch_info(
    user_id: str = Form(...),
    branch_name: str = Form(...)
):
    user_id = user_id[1:-1]
    if not await mydb.check_user_id_exist(user_id):
        return "USERID DOESN'T EXIST"

    return await exception_handle("SERVER ERROR WHILE GETTING OFFICE ADDRESS FROM PSQL", mydb.get_branch_info, branch_name)

@app.post('/get_img')
async def get_img(
    user_id: str = Form(...),
):
    user_id = user_id[1:-1]
    if not await mydb.check_user_id_exist(user_id):
        return "USERID DOESN'T EXIST"

    e = await exception_handle("SERVER ERROR WHILE RETRIEVING IMAGE FROM SERVER", mydb.get_img, user_id)
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

    return await exception_handle("SERVER ERROR WHILE RETRIEVING USER OVERVIEW DATA FROM PSQL", mydb.get_user_overview)


@app.exception_handler(Exception)
async def validation_exception_handler(request, err):
    base_error_message = f"Failed to execute: {request.method}: {request.url}"
    # Change here to LOGGER
    return JSONResponse(status_code=400, content={"message": f"{base_error_message}. Detail: {err}"})

@app.on_event("startup")
async def startup():
    await mydb.database.connect()
    await mydb.create()

@app.on_event("shutdown")
async def shutdown():
    await mydb.database.disconnect()

if __name__ == '__main__':
    uvicorn.run(app, port=5000)