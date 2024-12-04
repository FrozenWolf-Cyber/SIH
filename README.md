<div id="top"></div>

<br />
<div align="center">
    <img src="https://user-images.githubusercontent.com/57902078/188271435-600577ed-9f26-432f-87a1-30d90741d301.jpg" alt="Logo" width="25%" height="25%">
  </a>

  <p align="center"  style="text-align:center">
    <h1>I-Pravesh</h1>
    The most convenient and secure way to mark attendance.
    <br><br>
    <a href="http://35.89.24.75:5000/"><strong>Dashboard Demo »</strong></a> &nbsp;&nbsp;
    <a href="https://github.com/FrozenWolf-Cyber/SIH/blob/dl_models/README.md"><strong>Download App »</strong></a> &nbsp;&nbsp;
    <a href="https://github.com/FrozenWolf-Cyber/SIH/blob/dl_models/README.md"><strong>Model Results »</strong></a> &nbsp;&nbsp;</br>
    <a href="https://github.com/FrozenWolf-Cyber/SIH/tree/dl_models/saved_models"><strong>Download Models »</strong></a> &nbsp;&nbsp;
    <a href="https://github.com/FrozenWolf-Cyber/SIH/blob/dl_models/README.md"><strong>Preview »</strong></a> &nbsp;&nbsp;
  </p>
</div>


## Table of Contents
   * [About](About)
   * [Installation](Installation)
   * [Usage](Usage)

## About
i-Pravesh is a Smart Attendance Android App which uses a combination of edge face detection and recognition (MobileFaceNet + TensorFlow Lite) as the authentication biometric for recording attendance. Additionally, the app has been designed to ensure that an employee is within their office premises during attendance capture by analyzing the smartphone's geolocation.

All attendance data is updated in real-time with the help of an asynchronous server built using FastAPI and deployed in Heroku. The user data is encrypted and stored in a PostgreSQL database attached to the server instance. An user-friendly admin console has also been developed to oversee the attendance process.

The benefits of this app include flexible deployment, cost-efficiency, minimum footprint, and scalable architecture. The app has also been designed in a such a way to overcome several issues such as liveness detection, user authentication, and proxy attendance.


## Installation

### Server

Detailed server-side setup has been explained [here](https://github.com/FrozenWolf-Cyber/SIH/tree/master/server).

To clone the files used for the server, use the following commands:
```shell
git clone https://github.com/FrozenWolf-Cyber/SIH.git
cd SIH
cd server
```

That's it! Using these files, deploy your own server using Heroku, Azure or any other hosting platforms!

*Note:* _Please refer the above link to setup the environment variables before deployment._

### Dashboard

Refer [this](https://github.com/FrozenWolf-Cyber/SIH/tree/master/Web) for detailed deployment setup.

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

### Android App

Usage
=====

![image](https://frozenwolf-cyber.github.io/img/projects/sih.png)

