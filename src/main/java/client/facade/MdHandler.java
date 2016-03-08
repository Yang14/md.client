package client.facade;

import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Mr-yang on 16-3-8.
 */
public class MdHandler {
    private static Logger logger = LoggerFactory.getLogger("MdHandler");

    private ClientService client = new ClientServiceImpl();

    public static void main(String[] args) throws IOException {
        System.out.println("Please input a float number:");
        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        String str = buf.readLine();
        while(!str.equals("exit")){
            str = buf.readLine();
            //create file
            if (str.startsWith("cf")) {

            }
            //create dir
            if(str.startsWith("mkdir")){

            }

            //del file
            if(str.startsWith("df")){

            }

            //del dir
            if(str.startsWith("dd")){

            }

            //ls dir
            if (str.startsWith("ls")){

            }
            //find file
            if (str.startsWith("ff")){

            }
            //rename file
            if (str.startsWith("rf")){

            }
            //rename dir
            if (str.startsWith("rd")){

            }
            //help
            if (str.startsWith("-h")){
                System.out.println("file:create cf/ del df/ find ff/ rename rf");
                System.out.println("dir:create mkdir/ del dd/ find ls/ rename rd");
            }
        }
        buf.close();
    }

    private void createFile(String cmd){

    }

}
