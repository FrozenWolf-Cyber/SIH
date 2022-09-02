import json
import os
import shutil
import base64
import asyncio
import uvicorn
import logging
from encryption import encryption_algo
from messenger import mailman
from psql_database import Database
from fastapi import FastAPI, File, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import Response, JSONResponse

db_host = os.environ['DBHOST']
db_user =  os.environ['DBUSER']
db_psswrd = os.environ['DBPASS']
db_name = os.environ['DBNAME']

ADMIN_USERNAME = os.environ['ADMIN_USERNAME']
ADMIN_PSSWRD = os.environ['ADMIN_PSSWRD']

MESSENGER_MAILID = os.environ['MESSENGER_MAILID']
MESSENGER_PSSWRD = os.environ['MESSENGER_PSSWRD']

AES_KEY = os.environ['AES_KEY']
SHA256_KEY = os.environ['SHA256_KEY']
SALT = os.environ['SALT']

encryptor = encryption_algo(AES_KEY, SHA256_KEY, SALT.encode('utf-8'))
messenger = mailman(MESSENGER_MAILID, MESSENGER_PSSWRD)
# encryptor = pickle.load(open('encryptor.pkl', 'rb'))

mydb = Database(host = db_host, user = db_user, passwd = db_psswrd, database = db_name, encryptor=encryptor)


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

    name, designation, gender, branch_name = encryptor.AES_encrypt(name), encryptor.AES_encrypt(designation), encryptor.AES_encrypt(gender), encryptor.AES_encrypt(branch_name)
    data = [mail_id, name, designation, gender, branch_name, contact_no]

    sucess = False
    tries = 0
    while not sucess:
        try:
            e = await mydb.admin_signup(data)
            
        except:
            # print("SERVER ERROR WHILE UPDATING ADMIN SIGNUP IN PSQL")
            await asyncio.sleep(0.2)

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


@app.post('/send_otp')
async def send_otp(
    emp_no: str = Form(...)
):
    emp_no = emp_no[1:-1]

    mailid = await mydb.get_mail_id(emp_no)
    mailid = encryptor.AES_decrypt(mailid)

    try:
        otp = messenger.send_otp(mailid)
    except:
        messenger = mailman()
        otp = messenger.send_otp(emp_no, mailid)

    otp = encryptor.SHA256_encrypt(otp)
    await mydb.save_otp(emp_no, otp)

    return "SENT"


@app.post('/check_otp')
async def check_otp(
    emp_no: str = Form(...),
    otp: str = Form(...)
):

    emp_no = emp_no[1:-1]
    otp = otp[1:-1]


    if await mydb.check_otp(emp_no, encryptor.SHA256_encrypt(otp)):
        return "VERIFIED"

    else:
        return "NO"
    


@app.post('/signup')
async def signup(

    mobileid: str = Form(...),
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

    mobileid = encryptor.SHA256_encrypt(mobileid)
    data = [user_name, password, emp_no, mobileid, embed1, embed2, embed3]

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


@app.post('/reset_mobileid')
async def reset_mobileid(
    emp_no: str = Form(...),
    mobileid: str = Form(...)
):

    emp_no = emp_no[1:-1]
    mobileid = encryptor.SHA256_encrypt(mobileid)

    return await mydb.reset_mobileid(emp_no, mobileid)


@app.post('/admin_reset_mobileid')
async def admin_reset_mobileid(
    emp_no: str = Form(...),
):

    emp_no = emp_no[1:-1]

    return await mydb.admin_reset_mobileid(emp_no)


@app.post('/login')
async def login(
    user_name_or_mail_id: str = Form(...),
    type_of_login: str = Form(...),
    password: str = Form(...),
    mobileid: str = Form(...)
    
):
    mobileid = encryptor.SHA256_encrypt(mobileid)

    if user_name_or_mail_id == ADMIN_USERNAME and password == ADMIN_PSSWRD:
        return "ADMIN"

    password = encryptor.SHA256_encrypt(password)
    # user_name_or_mail_id = encryptor.SHA256_encrypt(user_name_or_mail_id)
        
    data = [user_name_or_mail_id, password, mobileid]

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

    data_args = 'name,mail_id,designation,emp_no,gender,branch_name,contact_no,check_in,check_out,in_latitude,in_longitude,out_latitude,out_longitude'.split(',')
    decrypt_for = 'name,mail_id,designation,gender,branch_name,contact_no'.split(',')
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


@app.post('/mass_registration')
async def mass_registration(
    data: str = Form(...),

):

    await mydb.mass_registration(json.loads(data))

    return "DONE"


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

@app.post('/get_img_website')
async def get_img_website(
    emp_no: str = Form(...),
):
    emp_no = emp_no[1:-1]
    if not await mydb.check_emp_no_signed(emp_no):
        return "EMPLOYEE NUMBER DOESN'T EXIST"

    e = await exception_handle("SERVER ERROR WHILE RETRIEVING IMAGE FROM SERVER", mydb.get_img, emp_no)
    if e == "SERVER ERROR WHILE RETRIEVING IMAGE FROM SERVER":
        return e

    # Return as b64 for website
    # decode_img = lambda y: Response(content=base64.b64decode(y), media_type="image/jpg")
    return e


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
        if k in ['name', 'designation', 'gender', 'branch_name']:
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
    await mydb.create()

@app.on_event("shutdown")
async def shutdown():
    await mydb.database.disconnect()

if __name__ == '__main__':
    uvicorn.run(app, port=5000)
