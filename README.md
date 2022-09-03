<div id="top"></div>

<br />
<div align="center">
    <img src="https://user-images.githubusercontent.com/57902078/188271435-600577ed-9f26-432f-87a1-30d90741d301.jpg" alt="Logo" width="30%" height="30%">
  </a>

  <p align="center"  style="text-align:center">
    <br><br> World's most convenient and secure way to mark attendance.
    <br><br>
    <a href="http://35.89.24.75:5000/"><strong>Dashbaord Demo »</strong></a> &nbsp;&nbsp;
    <a href="https://github.com/FrozenWolf-Cyber/SIH/blob/dl_models/README.md"><strong>Download App »</strong></a> &nbsp;&nbsp;
    <a href="https://github.com/FrozenWolf-Cyber/SIH/blob/dl_models/README.md"><strong>Model results »</strong></a> &nbsp;&nbsp;</br>
    <a href="https://github.com/FrozenWolf-Cyber/SIH/tree/dl_models/saved_models"><strong>Download Models »</strong></a> &nbsp;&nbsp;
    <a href="https://github.com/FrozenWolf-Cyber/SIH/blob/dl_models/README.md"><strong>Preview »</strong></a> &nbsp;&nbsp;
  </p>
</div>


Table of Contents
=================
   * [About](About)
   * [Installation](Installation)
   * [Usage](Usage)

About
============
The i-Pravesh app uses the TensorFlow Lite model to do face recognition to generate employee attendance by analyzing the current user geolocation. All the data is updated in real-time with the help of asynchronous servers deployed using Fastapi in Heroku. The user data is encrypted and stored in the PostgreSQL database. It also has a user-friendly admin dashboard website to oversee the attendance process.

It can be quickly scaled into Azure servers while requiring minimal compute resources and seamlessly integrating existing employee details in a company. It can also solve issues like liveness detection, user authentication, and proxy attendance.


Installation
============

### 1) Server

Detailed server deployment and environment varaible setup is explained [here](https://github.com/FrozenWolf-Cyber/SIH/tree/master/server)

```shell
git clone https://github.com/FrozenWolf-Cyber/SIH.git
cd SIH
cd server
```

That's it. Now you can deploy these files into Heroku, Azure or any other hosting platforms!

*Note:* _Please refer the above link to setup the environment variables before deployment_

### 2) Dashbaord

Refer [this](https://github.com/FrozenWolf-Cyber/SIH/tree/master/Web) for detailed deployment.

Create any basic instance in AWS and connect it with the instance using SSH for deployment.
Move to ```awsDeployee folder```.
```shell
git clone https://github.com/FrozenWolf-Cyber/SIH.git
cd SIH/Web/awsDeployee/
```

```shell
npm i
npm install pm2 -g
```
To deploy website in production mode.
```shell
pm2 start index.js
```

### 3) Android App

Usage
=====



