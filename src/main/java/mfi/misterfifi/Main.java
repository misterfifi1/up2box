package mfi.misterfifi;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.box.sdk.*;
import mfi.misterfifi.box.Box;
import mfi.misterfifi.slack.SlackNotification;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    @Parameter(names = {"-h", "-?", "--help"}, descriptionKey = "help", help = true)
    private boolean argHelp;

    @Parameter(names = {"-f", "--file"}, description = "The path to get the file to up", required = true)
    private String araFileNameUp;

    @Parameter(names = {"-n", "--folderName"}, description = "The name of the targeted Box folder", required = true)
    private String argFolderName;

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static BoxFolder folder = null;
    private static boolean isUploaded = false;

    private static String slackWebHook = null;
    private static String slackChannel = null;
    private static String slackUserName = null;

    private static final  String CONFIG_PROP = "config.properties";

    public static void main(String[] args) {
        //initialisation
        Main main = new Main();
        JCommander jc = JCommander.newBuilder()
                .addObject(main)
                .build();
        try {
            jc.parse(args);
        } catch (ParameterException e) {
            logger.error("Command not valid, please consult the Help");
            jc.usage();
            System.exit(0);
        }

        //Test if the config file exists
        isConfigExist(CONFIG_PROP, jc);

        //Test if the file to upload is accessible
        isConfigExist(main.araFileNameUp, jc);

        Configurations configs = new Configurations();
        Configuration config = null;
        try {
            config = configs.properties(new File(CONFIG_PROP));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        if (config == null ) System.exit(0);

        //Config DATA - Box
        List<String> boxCollaboratorEmails = Arrays.asList(config.getString("box.collaboratorEmail").split("\\s*,\\s*"));
        String boxConfigFile = config.getString("box.configFile");

        //Config DATA - Slack
        boolean isSlackEnabled = config.getBoolean("slack.isEnabled");
        slackWebHook = config.getString("slack.WebHook");
        slackChannel = config.getString("slack.Channel");
        slackUserName = config.getString("slack.UserName");
        File fileToUp = new File(main.araFileNameUp);

        //Test if the Box config file is accessible
        isConfigExist(boxConfigFile, jc);

        try {
            //Box Authentication
            Box box = new Box(boxConfigFile, fileToUp);
            box.startConnection();

            folder = box.getOrCreateBoxFolder(main.argFolderName);

            if (folder != null) {
                String fileID = getFileID(fileToUp.getName());
                if (fileID.equals("")) {
                    box.uploadNewFile(folder);
                } else {
                    box.uploadNewVersion(fileID);
                }
                isUploaded = true;
                up2Box(boxCollaboratorEmails);

                if (isSlackEnabled && slackChannel != null && slackUserName != null && slackWebHook != null)
                    sendToSlack(fileToUp);
            }
            logger.info("Upload completed!");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    private static void up2Box(List<String> boxCollaboratorEmails){
        for (String boxCollaboratorEmail: boxCollaboratorEmails) {
            if (isEmailAdress(boxCollaboratorEmail)) {
                if (!isCollaborating(boxCollaboratorEmail))
                    folder.collaborate(boxCollaboratorEmail, BoxCollaboration.Role.CO_OWNER);
            }else {
                logger.warn(boxCollaboratorEmail.concat(" not an email address! Will be ignored"));
            }
        }
    }

    private static void isConfigExist(String filePathString, JCommander jc){
        File f = new File(filePathString);
        if(!f.exists() || f.isDirectory()) {
            logger.error(filePathString.concat(" is not Accessible! The application will stop"));
            jc.usage();
            System.exit(0);
        }
    }

    private static boolean isEmailAdress(String email){
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
        Matcher m = p.matcher(email.toUpperCase());
        return m.matches();
    }

    private static String getFileID(String fileName) {
        String fileID = "";
        for (BoxItem.Info itemInfo : folder) {
            if (itemInfo instanceof BoxFile.Info) {
                BoxFile.Info fileInfo = (BoxFile.Info) itemInfo;
                if (fileInfo.getName().equals(fileName)) {
                    fileID = fileInfo.getID();
                }
            }
        }
        return fileID;
    }

    private static boolean isCollaborating(String boxCollaboratorEmail) {
        boolean isCollaborating = false;
        for (BoxCollaboration.Info collaborator : folder.getCollaborations()) {
            BoxCollaborator.Info collaboratorInfo = collaborator.getAccessibleBy();
            if (((BoxUser.Info) collaboratorInfo).getLogin().equals(boxCollaboratorEmail))
                isCollaborating = true;
        }
        return isCollaborating;
    }

    private static void sendToSlack(File fileToUp) {
        try {
            if (!slackChannel.contains("#"))
                slackChannel = "#".concat(slackChannel);
            SlackNotification slackNotification = new SlackNotification(slackWebHook, slackChannel, slackUserName);
            String message = String.format("The file: %s was uploaded successfully!", fileToUp.getName());
            if (!isUploaded)
                message = "Unfortunately the file %s was not uploaded!!!";
            slackNotification.sendMessage(message);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
