import os
from pydrive.auth import GoogleAuth
from pydrive.drive import GoogleDrive

class gdrive():
    def __init__(self):
        gauth = GoogleAuth()
        gauth.LocalWebserverAuth()
        self.drive = GoogleDrive(gauth)
        self.img_db_id = None
        self.user_folders_id = {}
        self.each_user_img_id = {}
        self.refresh()


    def refresh(self):
        file_list = self.drive.ListFile({'q': "'root' in parents and trashed=false"}).GetList()

        for file in file_list:
            if file['title'] == 'image_database':
                self.img_db_id = file['id']

        file_list = self.drive.ListFile({'q': f"'{self.img_db_id}' in parents and trashed=false"}).GetList()

        for file in file_list:
            self.user_folders_id[file['title']] = file['id']
            self.each_user_img_id[file['title']] = {}

            for images in self.drive.ListFile({'q': f"'{file['id']}' in parents and trashed=false"}).GetList():
                self.each_user_img_id[file['title']][images['title'].split('.')[0]] = images['id'] # save image in format center/left/right.png

    def download_user_img(self, user_id):
        if user_id not in os.listdir('img_db'):
            os.mkdir(f'img_db/{user_id}')

        for (img_name, img_id) in self.each_user_img_id[user_id].items():
            file_obj = self.drive.CreateFile()
            file_obj['id'] = img_id
            file_obj.GetContentFile(f'img_db/{user_id}/{img_name}.png')

    def upload_img_folder(self, user_id):
        folder_metadata = {
              'title': user_id,
              'parents': [{"id":self.img_db_id}],
              'mimeType': 'application/vnd.google-apps.folder'
            }
        
        folder = self.drive.CreateFile(folder_metadata)
        folder.Upload()

        self.user_folders_id[user_id] = folder['id']
        self.each_user_img_id[user_id] = {}

        for imgs in os.listdir(f'img_db/{user_id}'): # save image in format center/left/right.png
            file_obj = self.drive.CreateFile({'parents':[{'id':folder['id']}]})
            file_obj.SetContentFile(f"img_db/{user_id}/{imgs}")
            file_obj['title'] = imgs.split('.')[0]
            file_obj.Upload()

            self.each_user_img_id[user_id][file_obj['title']] = file_obj['id']


