package mfi.misterfifi.box;

import com.box.sdk.*;

import java.io.*;

public class Box {

    private BoxDeveloperEditionAPIConnection serviceAccountClient;
    private String config;
    private File fileToUpload;

    public Box(String jsonFile, File fileToUpload) {
        this.config = jsonFile;
        this.fileToUpload = fileToUpload;
    }

    public void startConnection() throws IOException {
        Reader reader = new FileReader(this.config);
        BoxConfig boxConfig = BoxConfig.readFrom(reader);
        this.serviceAccountClient = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
    }

    public BoxFolder getOrCreateBoxFolder(String folderName){
        String folderID = "";
        BoxFolder folder = BoxFolder.getRootFolder(this.serviceAccountClient);
        for (BoxItem.Info itemInfo : folder) {
            if (itemInfo instanceof BoxFolder.Info) {
                BoxFolder.Info folderInfo = (BoxFolder.Info) itemInfo;
                if (folderInfo.getName().equals(folderName)){
                    folderID = folderInfo.getID();
                }
            }
        }

        if (folderID.equals("")) {
            BoxFolder rootFolder = BoxFolder.getRootFolder(this.serviceAccountClient);
            BoxFolder.Info childFolderInfo = rootFolder.createFolder(folderName);
            return new BoxFolder(this.serviceAccountClient, childFolderInfo.getID());
        }else{
            return new BoxFolder(serviceAccountClient, folderID);
        }
    }

    public void uploadNewVersion(String fileID) throws FileNotFoundException {
        BoxFile file = new BoxFile(this.serviceAccountClient, fileID);
        FileInputStream stream = new FileInputStream(this.fileToUpload.getAbsolutePath());
        file.uploadNewVersion(stream);
    }

    public void uploadNewFile(BoxFolder folder) throws FileNotFoundException {
        FileInputStream stream = new FileInputStream(this.fileToUpload.getAbsolutePath());
        folder.uploadFile(stream, this.fileToUpload.getName());
    }
}
