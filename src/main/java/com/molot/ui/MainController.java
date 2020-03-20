package com.molot.ui;

import com.molot.Core;
import com.molot.lowlevel.rw.ReaderWriter;
import com.molot.lowlevel.rw.ReaderWriterFactory;
import com.molot.util.Params;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController  {
    private int currentSpeed;

    private double[] tmpValues;
    byte[] buf;


    public byte index=0;
    public int flagPoint=0;

    public boolean frequencyFlag = false;
    public boolean  filterFlag = false;
    public boolean  serverFlag = false;
    public int freq=0;
    public int filter=0;

    private int step_count=0;

    private   int step=0;
    String point= "1024";
    javafx.scene.image.Image image1;

    double scaleGra = 1;
    int flagDraw=0;
    public static int frequencyWinradio = 0 ;
    public static int filterWinradio = 0 ;

    private boolean running = true;

    @FXML
    private Button button_connectEth;

    @FXML
    private Button button_send;



    @FXML
    private TextArea textArea_send;



    @FXML
    public TextField textIp;

    @FXML
    public TextField textPort;


    @FXML
    public CheckBox checkBoxServer;




    @FXML
    public void initialize(){
        Core.config().name = "Панель управління";
        //Core.config().ui = window.getUi();
        Core.getInstance().setRunning(true);
        System.out.println("initialize()");

        new UpdateUIThread().start();

        button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        button_connectEth.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("its me");
                String ip = textIp.getText().toString();
                int port = Integer.parseInt(textPort.getText());
                int newSpeed = 250;
                boolean firstInit = Core.config().channel == null;

                        Params.SPEED.setSpeed(newSpeed);
                        Params.SETTINGS.putInt("work-speed", Params.SPEED.getSpeed());
                        Params.RUNTIME.putInt("work-mode", Params.SPEED.getMode());
                        Params.SETTINGS.save();


                        if (firstInit) {
                            ReaderWriter newReaderWriter = ReaderWriterFactory.getInstance().getReaderWriter();
                            Core.setChannel(newReaderWriter);
                            //Core.log("Встановлено режим роботи " + Params.SPEED.getDescription());
                        } else {
                            Core.config().channel.reinit();
                            //Core.log("Режим роботи змінено на " + Params.SPEED.getDescription());
                        }

                        currentSpeed = newSpeed;

                        Core.setSynchronized(false);


                        Core.getInstance().reset();




            }
        });
    }


    class UpdateUIThread extends Thread {
        @Override
        public void run() {
            while(running) {
                if (Core.isReady()) {

                    updateMessages();
                    updatePacketStats();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }
        }

        private void updateMessages() {
//            final MessageModel model = Core.messages().getMessageModel();
//            if (model.isModelChanged()) {
//                SwingUtilities.invokeLater(new Runnable() {
//                    public void run() {
//                        setMessageHistory(model);
//                        model.gotAllData();
//                        messageHistoryScroll.getVerticalScrollBar().setValue(messageHistoryScroll.getVerticalScrollBar().getMaximum() * 2);
//                        messageHistoryScroll.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
//
//                        SwingUtilities.invokeLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                messageHistory.updateUI();
//                                messageHistoryScroll.updateUI();
//                                messageHistoryScroll.repaint();
//                            }
//                        });
//                    }
//                });
//            }
        }

        private void updatePacketStats() {
//            int sentPacketCount = Core.stats().getInt("sent_packet_count");
//            int totalPacketCount = Core.stats().getInt("received_packet_count");
//            int brokenPacketCount = Core.stats().getInt("received_bad_packet_count");
//            int repairedPacketCount = Core.stats().getInt("received_bad_and_corrected_packet_count");
//
//            //"Пакети, передано/прийнято, помилкових/невиправлених:"
//            String message = sentPacketCount + "/" + totalPacketCount + ", " + brokenPacketCount + "/" + (brokenPacketCount - repairedPacketCount);
//            packetStatsValue.setText(message);
        }


    }






}
