import math
import random
import smtplib
from email.message import EmailMessage

class mailman():
    def __init__(self, mailid = 'ipravesh.sih@gmail.com' , password = "xdkatsbalbwimapl"):
        self.smtp = smtplib.SMTP_SSL('smtp.gmail.com', 465, timeout=None)
        self.smtp.login(mailid ,password)
        self.allowed = "0123456789"
        self.mailid = mailid

    def generate_otp(self):
        OTP=""
        for i in range(6):
            OTP+=self.allowed[math.floor(random.random()*10)]

        return OTP

    def send_otp(self, mailid):
        msg = EmailMessage()
        msg['Subject'] = "OTP for iPravesh attendance app"
        msg['From'] = self.mailid
        # msg['To'] = 
        msg['To'] = 'blackvenky21@gmail.com'

        otp = self.generate_otp()

        msg.set_content(f'''
<!DOCTYPE html>
<html>
    <body>
<div style="font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2">
  <div style="margin:50px auto;width:70%;padding:20px 0">
    <div style="border-bottom:1px solid #eee">
      <a href="" style="font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600">i-Pravesh</a>
    </div>
    <p style="font-size:1.1em">Hi,</p>
    <p>Thank you for choosing i-Pravesh. Use the following OTP to complete your Sign Up procedures. OTP is valid for 5 minutes</p>
    <h2 style="background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;">{otp}</h2>
    <p style="font-size:0.9em;">Regards,<br />i-Pravesh</p>
    <hr style="border:none;border-top:1px solid #eee" />
    <div style="float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300">
      <p>i-Pravesh Inc</p>
      <p>1600 Amphitheatre Parkway</p>
      <p>California</p>
    </div>
  </div>
</div>
    </body>
</html>
''', subtype='html')

        self.smtp.send_message(msg)

        return otp
