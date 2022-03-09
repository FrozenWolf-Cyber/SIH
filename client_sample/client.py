import requests
import time
## SIGNUP
url = 'http://127.0.0.1:5000/signup'
### mail_id user_name password name age address contact_no blood_grp
data = {'mail_id' : 'p@gmail.com',
        'user_name' : 'p',
        'password' : 'sp',
        'name' : 'Sundar Pichai',
        'age' : '31',
        'address' : 'India',
        'contact_no' : '100',
        'blood_grp' : 'B+'}

# files = {"img_left":open("sp/sp_left.png", "rb"),
#          "img_right":open("sp/sp_right.png", "rb"),
#          "img_center":open("sp/sp_center.png", "rb")}
files = [('files', ("img_left", open("sp/sp_left.png", "rb"), 'image/png')),
         ('files', ("img_right", open("sp/sp_right.png", "rb"), 'image/png')),
         ('files', ("img_center", open("sp/sp_center.png", "rb"), 'image/png'))]

x = requests.post(url, data= data, files=files)
print(x.text)



## LOGIN
url = 'http://127.0.0.1:5000/login'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_name_or_mail_id' : 'p@gmail.com',
        'type_of_login' : 'mail_id', #user_name or mail_id
        'password' : 'sp',}

x = requests.post(url, data= data)
print(x.text)

user_id = x.text

## GET INFO
url = 'http://127.0.0.1:5000/get_info'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_id' : user_id}

x = requests.post(url, data= data)
print(x.text)

## CHECK TABLES
url = 'http://127.0.0.1:5000/master_cmd'
### mail_id user_name password name age address contact_no blood_grp
data = {'cmd' : "SELECT * FROM USER_INFO"}

x = requests.post(url, data= data)
print(x.text)

data = {'cmd' : "SELECT * FROM USER_LOGIN"}

x = requests.post(url, data= data)
print(x.text)

data = {'cmd' : "SELECT * FROM USER_LOG"}

x = requests.post(url, data= data)
print(x.text)

## VERIFY
url = 'http://127.0.0.1:5000/verify'
files = {'image': open("sp/sp_left.png", 'rb')}

x = requests.post(url, files= files, data= {'user_id' : user_id})

print(x.text)

url = 'http://127.0.0.1:5000/status'
x = requests.post(url, data= {'user_id' : user_id})

while True:
    print(x.text)
    if x.text != 'WAIT':
        break
    time.sleep(0.3)

## UPDATE LOG
time.sleep(0.1)
url = 'http://127.0.0.1:5000/update_log'
x = requests.post(url, data= {'user_id' : user_id, "date_time":"10:53:56@06.03.2022-14:53:56@06.03.2022 10:53:56@06.03.2022-14:53:56@06.03.2022 "})
print(x.text)


# ## CHECK OUT 
# time.sleep(0.1)
# url = 'http://127.0.0.1:5000/check_out'
# x = requests.post(url, data= {'user_id' : user_id,"date_time": "10:53:56@06.03.2022"})
# print(x.text)


## CHECK TABLES
url = 'http://127.0.0.1:5000/master_cmd'
data = {'cmd' : "SELECT * FROM USER_LOG"}

x = requests.post(url, data= data)
print(x.text)