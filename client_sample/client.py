import requests
import time

## SIGNUP
url = 'http://127.0.0.1:5000/signup'
### mail_id user_name password name age address contact_no blood_grp
data = {'mail_id' : 'sp@gmail.com',
        'user_name' : 'sp',
        'password' : 'sp',
        'name' : 'Sundar Pichai',
        'age' : '31',
        'address' : 'India',
        'contact_no' : '100',
        'blood_grp' : 'B+'}

files = {"img_left":open("sp/sp_left.jpg", "rb"),
         "img_right":open("sp/sp_right.jpg", "rb"),
         "img_center":open("sp/sp_center.jpg", "rb")}

x = requests.post(url, data= data, files=files)
print(x.content)



## LOGIN
url = 'http://127.0.0.1:5000/login'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_name_or_mail_id' : 'sp@gmail.com',
        'type_of_login' : 'mail_id', #user_name or mail_id
        'password' : 'sp',}

x = requests.post(url, data= data)
print(x.content)

user_id = x.content.decode("utf-8") 

## GET INFO
url = 'http://127.0.0.1:5000/get_info'
### mail_id user_name password name age address contact_no blood_grp
data = {'user_id' : user_id}

x = requests.post(url, data= data)
print(x.content)


# ## VERIFY
# url = 'http://127.0.0.1:5000/verify'

# files = {'image': open("sp/sp_left.jpg", 'rb')}

# x = requests.post(url, files= files, data= {'user_id' : 'jeff'})

# print(x.content)

# while True:

#     time.sleep(0.3)
#     url = 'http://127.0.0.1:5000/status'
#     x = requests.post(url, data= {'user_id' : 'jeff'})

#     print(x.text)
#     if x.text != 'WAIT':
#         print(x.text)
#         break

url = 'http://127.0.0.1:5000/verify'
files = {'image': open("sp/sp_left.jpg", 'rb')}

x = requests.post(url, files= files, data= {'user_id' : user_id})

print(x.content)

while True:

    time.sleep(0.3)
    url = 'http://127.0.0.1:5000/status'
    x = requests.post(url, data= {'user_id' : user_id})

    print(x.text)
    if x.text != 'WAIT':
        print(x.text)
        break