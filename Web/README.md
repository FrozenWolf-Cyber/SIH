

# Dashboard Deployment (AWS)

### Step 1: Setup AWS for deployment
Create any basic instance in AWS and connect it with the instance using SSH for deployment.

### Step 2: Setup Node JS
Move to ```awsDeployee``` folder.
```shell
git clone https://github.com/FrozenWolf-Cyber/SIH.git
cd SIH/Web/awsDeployee/
```
Install Node Js using NVM. Use [NVM installation guide](https://github.com/nvm-sh/nvm#installing-and-updating) and [Node Installation guide](https://github.com/nvm-sh/nvm#usage) for your references.
  
  _Note: We have tested out our website using Node v8.15.0 and is recommended to replicate our results, install other versions at your own risk_

Install production manager
  ```shell
  npm i
  npm install pm2 -g
  ```

### Step 3: Deployment
To deploy website in production mode.
```shell
   pm2 start index.js
```
