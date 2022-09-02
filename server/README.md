# SERVER

To setup the server :

### Step 1: Go to server folder
```shell
git clone https://github.com/FrozenWolf-Cyber/SIH.git
cd SIH
cd server
```

### Step 2: Set the environment variables

Set the environment varaibles of the server in which the web app is hosted for the following variables accordingly:

```DBHOST```&nbsp;:&nbsp;&nbsp; PSQL Host name.<br />
```DBUSER```&nbsp;:&nbsp;&nbsp; PSQL User name.<br />
```DBPASS```&nbsp;:&nbsp;&nbsp; PSQL User password.<br />
```DBNAME```&nbsp;:&nbsp;&nbsp; PSQL Database name.<br />
<br />
```ADMIN_USERNAME```&nbsp;:&nbsp;&nbsp; Any string of your choice.<br />
```ADMIN_PSSWRD```&nbsp;:&nbsp;&nbsp; Any string of your choice.<br />
<br /><br />
```MESSENGER_MAILID```&nbsp;:&nbsp;&nbsp; Mail id from which you would like to send the OTP.<br />
```MESSENGER_PSSWRD```&nbsp;:&nbsp;&nbsp; App password generated for the mail id above.  <br />
<br />
Check [this](https://levelup.gitconnected.com/an-alternative-way-to-send-emails-in-python-5630a7efbe84) to generate your password for the mail id used for OTP
<br /><br />
```AES_KEY```&nbsp;:&nbsp;&nbsp; Any string of your choice which will be used as the AES key.<br />
```SHA256_KEY```&nbsp;:&nbsp;&nbsp; Any string of your choice which will be used as the SHA256 key.<br />
```SALT```&nbsp;:&nbsp;&nbsp; Any randomly generate bytes like: ```\xf6}\xaep\x80\xf8\xc0\xfe\xc9r\xe9\xa4\xcc_\x03\xa5```<br />
<br />
<br />

#### Example in Heroku:
You can set environment varaible in settings in Heroku dashboard like given below:<br /><br />
![demo](https://user-images.githubusercontent.com/57902078/188234922-7439b5c4-3ae9-4772-8f8e-d2eaaf12b480.jpeg) <br /><br />
<br />You can get the PSQL credential by going to settings in PSQL addon pack in Heroku like given below:<br /><br />
![demo2](https://user-images.githubusercontent.com/57902078/188235042-17d4969d-2b78-43f6-a693-8923b5c3d678.jpg)

