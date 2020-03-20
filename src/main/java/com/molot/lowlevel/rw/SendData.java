package com.molot.lowlevel.rw;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.EmptyBorder;

//import utils.ByteUtils;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataListener;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataProcessor;
import com.molot.lowlevel.rw.enums.ConfigirationFreq;
//import ui.com.molot.util.FrameTools;
import com.molot.util.Params;

/**
 * Created by integer on 3/12/20.
 */

public class SendData extends JDialog implements ByteDataListener {

    private static final long serialVersionUID = 7072133681727767472L;
    private JComboBox<ConfigirationFreq> freqConfComboBox;
    private JLabel freqConfLabel;
    private int currentByteIndex;
    private int[] receivedData = new int[10];
    private byte[] rawReceivedData = new byte[10];
    private UpdateThread updateThread;
    private ReaderWriter readerWriter;
    ConfigirationFreq  confFreq ;
    private ByteDataProcessor byteDataProcessor;

    class UpdateThread extends Thread {
        private boolean needStop;

        public void run() {
            while(true) {
                if (needStop) {
                    return;
                }

                if (readerWriter != null && byteDataProcessor != null && readerWriter.getReader().canRead()) {
                    while(readerWriter.getReader().canRead()) {
                        boolean nextBit = readerWriter.getReader().readBit();
                        byteDataProcessor.addBit(nextBit);
                    }
                }

                try { Thread.sleep(500);} catch (InterruptedException e) { e.printStackTrace();}
            }
        }

        public void stopThisThread() {
            needStop = true;
        }
    };


    private final JPanel contentPanel = new JPanel();
    private JFormattedTextField maskText;
    private JFormattedTextField portText;
    private JFormattedTextField ipAddress;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SendData dialog = new SendData();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public SendData() {
        setTitle("Налаштування конфігурації частоти");

        addWindowListener(new WindowClosingListener());

        setBounds(100, 100, 450, 200);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 0));
        getContentPane().add(contentPanel, BorderLayout.CENTER);


        maskText = new JFormattedTextField();
        freqConfLabel = new JLabel("Конфігірація частот:");
        freqConfComboBox = new JComboBox(ConfigirationFreq.values());

        //JLabel label_1 = new JLabel("Порт:");

        //portText = new JFormattedTextField();

        GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
        gl_contentPanel.setHorizontalGroup(
                gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(freqConfLabel)
                                        )
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(freqConfComboBox, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                        )
                                .addContainerGap())
        );
        gl_contentPanel.setVerticalGroup(
                gl_contentPanel.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(gl_contentPanel.createSequentialGroup()
                                .addGroup(gl_contentPanel.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(freqConfLabel)
                                        .addComponent(freqConfComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))

                        )
        );
        contentPanel.setLayout(gl_contentPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBorder(new EmptyBorder(0, 5, 5, 15));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

            JButton refreshButton = new JButton("Оновити");
            refreshButton.addActionListener(new RefreshListener());
            buttonPane.add(refreshButton);

            Component horizontalGlue = Box.createHorizontalGlue();
            buttonPane.add(horizontalGlue);

            JButton saveButton = new JButton("Зберегти");
            saveButton.addActionListener(new SaveListener());
            buttonPane.add(saveButton);
        }

        //FrameTools.situateOnCenter(this);




    }
    class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ConfigirationFreq confFreq = (ConfigirationFreq) freqConfComboBox.getSelectedItem();
            System.out.println("setings:"+Params.SETTINGS.getString("frequency-config"));
			Params.SETTINGS.putString("frequency-config",confFreq.name());

            readerWriter.writeByteAsIs((byte)6);
            readerWriter.writeByteAsIs((byte)confFreq.getConf());
            readerWriter.flush();

            Params.SETTINGS.save();

        }
        }

    public void setReaderWriter(final ReaderWriter readerWriter) {
        byteDataProcessor = new ByteDataProcessor(this);
        this.readerWriter = readerWriter;
        readerWriter.workInRawMode(true);

        readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("close-in-out"));

        if(updateThread == null) {
            updateThread = new UpdateThread();
            updateThread.start();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }
    class WindowClosingListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (updateThread != null) {
                updateThread.stopThisThread();
                updateThread = null;

                readerWriter.close();
                ReaderWriterFactory.getInstance().close();
            }

            dispose();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            updateCheckBox();
            super.windowActivated(e);
        }
    }


    class RefreshListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateCheckBox();
        }
    }


    public void updateCheckBox() {

        String selectItem = Params.SETTINGS.getString("frequency-config");
        confFreq = (ConfigirationFreq) confFreq.valueOf(selectItem);
        freqConfComboBox.setSelectedItem(confFreq);
    }
    @Override
    public void byteReceived(byte b) {

    }
}
