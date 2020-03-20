package com.molot.lowlevel.rw;

import com.google.common.primitives.Ints;
import com.molot.common.utils.ByteUtils;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataListener;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataProcessor;
import com.molot.util.Params;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import ua.com.integer.ui.com.molot.util.FrameTools;

public class EthernetSettingsDialog extends JDialog implements ByteDataListener {
	private static final long serialVersionUID = 7072133681727767472L;

	private int currentByteIndex;
	private int[] receivedData = new int[10];
	private byte[] rawReceivedData = new byte[10];
	
	private ReaderWriter readerWriter;
	
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
	private UpdateThread updateThread;

	private final JPanel contentPanel = new JPanel();
	private JFormattedTextField maskText;
	private JFormattedTextField portText;
	private JFormattedTextField ipAddress;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			EthernetSettingsDialog dialog = new EthernetSettingsDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EthernetSettingsDialog() {
		setTitle("Налаштування Ethernet");
		addWindowListener(new WindowClosingListener());
		
		setBounds(100, 100, 450, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblIp = new JLabel("IP адреса:");
		
		ipAddress = new JFormattedTextField();
		ipAddress.setColumns(12);
		
		JLabel label = new JLabel("Маска:");
		
		maskText = new JFormattedTextField();
		
		//JLabel label_1 = new JLabel("Порт:");
		
		//portText = new JFormattedTextField();
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblIp)
						.addComponent(label))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(ipAddress, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
						.addComponent(maskText, GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIp)
						.addComponent(ipAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(maskText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
	
	public void setReaderWriter(final ReaderWriter readerWriter) {
		byteDataProcessor = new ByteDataProcessor(this);
		this.readerWriter = readerWriter;
		readerWriter.workInRawMode(true);
		
		readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("close-in-out"));
		
		if(updateThread == null) {
			updateThread = new UpdateThread();
			updateThread.start();
		}
		
		updateEthernetParams();

	}
	
	public void updateEthernetParams() {
		if (readerWriter == null) {
			return;
		}
		
		ipAddress.setText("");
//		portText.setText("");
		maskText.setText("");
		
		byteDataProcessor.reset();
		readerWriter.reset();
		
		currentByteIndex = 0;
		readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("ethernet-get-params"));
	}

	@Override
	public void byteReceived(byte b) {
		
		if (currentByteIndex >= 10) {
			return;
		}
		rawReceivedData[currentByteIndex] = b;
		
		int value = Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, b);

		receivedData[currentByteIndex] = value;
		
		currentByteIndex++;
		
		if (currentByteIndex == 10) {
			ipAddress.setText(getIpFromParts(receivedData, 0));
			maskText.setText(getIpFromParts(receivedData, 4));
			
			int port = Ints.fromBytes((byte) 0, (byte) 0, rawReceivedData[8], rawReceivedData[9]);
			//portText.setText(String.valueOf(port));
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
			updateEthernetParams();
			super.windowActivated(e);
		}
	}
	
	class SaveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean ipValid = checkInputData(ipAddress);
			if (!ipValid) {
				JOptionPane.showMessageDialog(null, "IP адреса введена невірно!");
				return;
			}
			
			boolean maskValid = checkInputData(maskText);
			if (!maskValid) {
				JOptionPane.showMessageDialog(null, "Маска введена невірно!");
				return;
			}
			

			
			//init command
			readerWriter.writeByteAsIs(Params.PROTOCOL.getByte("ethernet-set-params"));
			
			//send ip
			int[] ipParts = getIpParts(ipAddress.getText());
			for(int part: ipParts) {
				readerWriter.writeByteAsIs(ByteUtils.getLastByteFromInt(part));

			}
			
			//send mask
			int[] maskParts = getIpParts(maskText.getText());
			for(int part: maskParts) {
				readerWriter.writeByteAsIs(ByteUtils.getLastByteFromInt(part));
			}
			
			//send port
			byte[] portBytes = {2,3};
			for(byte portByte: portBytes) {
				readerWriter.writeByteAsIs(portByte);
			}

			readerWriter.flush();
			
			JOptionPane.showMessageDialog(null, "Налаштування збережені!");
		}
	}
	
	private int[] getIpParts(String text) {
		int[] result = new int[4];
		String[] parts = text.split("\\.");
		for(int i = 0; i < 4; i++) {
			if (i <= parts.length - 1) {
				result[i] = Integer.parseInt(parts[i]);
			} else {
				result[i] = -1;
			}
		}
		return result;
	}
	
	private String getIpFromParts(int[] array, int offset) {
		String result = "";
		for(int i = offset; i < offset + 4; i++) {
			result += array[i];
			if (i < offset + 3) {
				result += ".";
			}
		}
		return result;
	}
	
	private boolean checkInputData(JFormattedTextField textField) {
		int[] ipParts = getIpParts(textField.getText());
		for(int ipPart: ipParts) {
			if (ipPart < 0 || ipPart > 255) {
				return false;
			}
		}
		return true;
	}
	
	class RefreshListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateEthernetParams();
		}
	}
}
