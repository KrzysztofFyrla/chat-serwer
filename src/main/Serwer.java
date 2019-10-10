package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Serwer {

    ArrayList klientArrayList;
    PrintWriter printWriter;

    public static void main(String[] args) {

         Serwer s = new Serwer();
         s.startSerwer();
    }

    //metoda starująca serwer
    public void startSerwer() {
        klientArrayList = new ArrayList();

        try {

            //nasłuchiwanie portu
            ServerSocket serverSocket = new ServerSocket(5000);

            //wszystkie połączenia przychodzące na porcie 5000 będą akceptowane
            Socket socket = serverSocket.accept();
            System.out.println("Słucham: " + serverSocket);

            /*przekazujemy do print writera strumień wyjściowy jaki udostępnia nam socket, poprzez niego będziemy
            wysyłać do klientów komunikaty, które z jakiegoś konkretnego klienta przyjdą*/
            printWriter = new PrintWriter(socket.getOutputStream());
            klientArrayList.add(printWriter);

            Thread t = new Thread(new SerwerKlient(socket));
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //klasa wewnetrzna (watki)
    class SerwerKlient implements Runnable {

        //sluzy do przechwycenia tego co przyjdzie
        Socket socket;
        //odczytuje to co klienci przysylaja
        BufferedReader bufferedReader;

        //kontruktor
        public SerwerKlient(Socket socketKlient) {
            try {
                System.out.println("Połączony");
                socket = socketKlient;
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String str;
            PrintWriter pw;

                try {
                    //jezeli cos wpadnie a nie jest nullem zostanie odczytane
                    while ((str = bufferedReader.readLine()) != null) {
                        System.out.println("Odebrano >> " + str);

                        /*iterator przejdzie po liscie klientow i dopóki nie osiagniemy konca tej listy to do kazdego
                        z nich bedziemy rozsylac to co odebralismy*/
                        Iterator it = klientArrayList.iterator();
                        while (it.hasNext()) {
                            pw = (PrintWriter) it.next();
                            pw.println(str);
                            pw.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }
}
