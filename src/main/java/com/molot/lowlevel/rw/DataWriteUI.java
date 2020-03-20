package com.molot.lowlevel.rw;

import com.google.common.primitives.Ints;
import com.molot.lowlevel.channel.IReadStream;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataListener;
import com.molot.lowlevel.rw.data.input.byteprocessor.ByteDataProcessor;
import com.molot.util.ByteUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

//import ua.com.integer.packet.com.molot.util.receive.ReceivePacketDialog;
//import ua.com.integer.packet.com.molot.util.send.CreatePacketDialog;
//import ua.com.integer.ui.com.molot.util.FrameTools;

public class DataWriteUI extends JDialog implements ByteDataListener {
	private static final long serialVersionUID = 2744436332868610215L;
	private final JPanel contentPanel = new JPanel();
	private JTextArea receivedText;
	private JTextField toSendTextArea;
	
	int countOfReadNumbers = 0;
	private JPanel sendPanel;
	private JComboBox<String> columnCountBox;
	int maxCountOfReadNumbers = 30;
	private JTextField textField;
	
	private JButton btnStart;
	private JButton btnClear;
	
	private ByteDataProcessor byteDataProcessor = new ByteDataProcessor(this);
	private ReaderWriter readerWriter;
	
	//private ReceivePacketDialog receivePacketDialog = new ReceivePacketDialog();
	
	private JCheckBox rawModeBox;
	private JCheckBox bitSeqCheckbox;
	private JCheckBox catchPacketsCheckbox;
	private JCheckBox receiveAsBinaryCheckbox;
	private JCheckBox clearAfterSend;
	private JTextField findText;
	
	private boolean needRun = true;
	
	class UpdateRunnable implements Runnable {
		public void run() {
			while(needRun) {
				updateReceivedData();
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void updateReceivedData() {
			if (readerWriter != null && readerWriter.getReader().canRead()) {
				IReadStream readStream = readerWriter.getReader();
				
				while(readStream.canRead()) {
					boolean bit = readStream.readBit();
					
					if (receiveAsBinaryCheckbox.isSelected()) {
						addByteToReceivedTextArea(bit ? 1 : 0);
					} else {
						byteDataProcessor.addBit(bit);
					}
					
					if (catchPacketsCheckbox.isSelected()) {
						//receivePacketDialog.addBit(com.molot.bit);
					}
				}
			}
		}
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DataWriteUI dialog = new DataWriteUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public DataWriteUI() {
		new Thread(new UpdateRunnable()).start();
		
		addWindowListener(new WindowClosingListener());
		
		setBounds(100, 100, 1000, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel dataPanel = new JPanel();
		contentPanel.add(dataPanel, BorderLayout.CENTER);
		dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(1.0);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		dataPanel.add(splitPane);
		
		JPanel receivedPanel = new JPanel();
		splitPane.setLeftComponent(receivedPanel);
		receivedPanel.setBorder(new TitledBorder(null, "\u041F\u0440\u0438\u0439\u043D\u044F\u0442\u0456 \u0434\u0430\u043D\u0456", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		receivedPanel.setLayout(new BorderLayout(0, 0));
		
		receivedText = new JTextArea();
		receivedText.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 14));
		receivedText.setColumns(8);
		JScrollPane receiveScroll = new JScrollPane(receivedText);
		receivedPanel.add(receiveScroll, BorderLayout.CENTER);
		
		JPanel controlPanel = new JPanel();
		receiveScroll.setColumnHeaderView(controlPanel);
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("К-сть колонок:");
		controlPanel.add(label);
		
		columnCountBox = new JComboBox<String>();
		columnCountBox.addActionListener(new ChangeColumnCountListener());
		columnCountBox.setModel(new DefaultComboBoxModel<String>(new String[] {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"}));
		columnCountBox.setSelectedIndex(1);
		columnCountBox.setEditable(true);
		controlPanel.add(columnCountBox);
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ClearListener());
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		controlPanel.add(horizontalStrut);
		controlPanel.add(clearButton);
		
		JPanel receivedToolPanel = new JPanel();
		receivedPanel.add(receivedToolPanel, BorderLayout.SOUTH);
		receivedToolPanel.setLayout(new BoxLayout(receivedToolPanel, BoxLayout.X_AXIS));
		
		receiveAsBinaryCheckbox = new JCheckBox("Binary");
		receiveAsBinaryCheckbox.addActionListener(new BinaryCheckboxListener());
		receivedToolPanel.add(receiveAsBinaryCheckbox);
		
		catchPacketsCheckbox = new JCheckBox("Catch packets");
		receivedToolPanel.add(catchPacketsCheckbox);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		receivedToolPanel.add(horizontalStrut_2);
		
		findText = new JTextField();
		findText.setText("01111110");
		receivedToolPanel.add(findText);
		findText.setColumns(10);
		
		JButton findButton = new JButton("Find");
		findButton.addActionListener(new FindListener());
		receivedToolPanel.add(findButton);
		
		sendPanel = new JPanel();
		splitPane.setRightComponent(sendPanel);
		sendPanel.setBorder(new TitledBorder(null, "\u0412\u0456\u0434\u043F\u0440\u0430\u0432\u043B\u0435\u043D\u0456 \u0434\u0430\u043D\u0456", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sendPanel.setLayout(new BorderLayout(0, 0));
		
		toSendTextArea = new JTextField();
		toSendTextArea.addKeyListener(new SendBitsListener());
		JScrollPane scrollPane = new JScrollPane(toSendTextArea);
		sendPanel.add(scrollPane);
		
		JPanel buttonPanel = new JPanel();
		sendPanel.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		btnStart = new JButton("Send");
		btnStart.addActionListener(new SendBitsListener());
		
		rawModeBox = new JCheckBox("Raw mode");
		buttonPanel.add(rawModeBox);
		rawModeBox.addActionListener(new RawModeListener());
		
		bitSeqCheckbox = new JCheckBox("Send as bits");
		buttonPanel.add(bitSeqCheckbox);
		
		clearAfterSend = new JCheckBox("Clear after send");
		buttonPanel.add(clearAfterSend);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		buttonPanel.add(horizontalGlue_1);
		buttonPanel.add(btnStart);
		
		btnClear = new JButton("Clear");
		btnClear.addActionListener(new StopTransferListener());
		btnClear.addActionListener(new ClearTransferListener());
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		buttonPanel.add(horizontalStrut_1);
		buttonPanel.add(btnClear);
	
		textField = new JTextField();
		textField.addKeyListener(new SendOnEnterPressListener());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
		}
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("Модем");
		menuBar.add(menu);
		
		JMenuItem settingsMenuItem = new JMenuItem("Налаштування");
		settingsMenuItem.addActionListener(new SettingsListener());
		menu.add(settingsMenuItem);
		
		JMenu menuPackets = new JMenu("Пакети");
		menuBar.add(menuPackets);
		
		JMenuItem menuSendPacket = new JMenuItem("Відправити пакет");
		menuSendPacket.addActionListener(new CreatePacketListener());
		menuPackets.add(menuSendPacket);
	}

	private void addByteToReceivedTextArea(int i) {
		String item = Integer.toString(i);
		receivedText.append(normalize(item));
		
		countOfReadNumbers++;

		if (receiveAsBinaryCheckbox.isSelected() && countOfReadNumbers % 8 == 0) {
			receivedText.append(" ");
		}
		
		if (receiveAsBinaryCheckbox.isSelected()) {
			if (countOfReadNumbers >= maxCountOfReadNumbers * 8) {
				receivedText.append("\n");
				countOfReadNumbers = 0;
			}
		} else {
			if (countOfReadNumbers >= maxCountOfReadNumbers) {
				receivedText.append("\n");
				countOfReadNumbers = 0;
			}
		}
	}
	
	private String normalize(String item) {
		if (receiveAsBinaryCheckbox.isSelected()) {
			return item;
		}
		
		int additionalSpaceCount = 4 - item.length();
		for(int i = 0; i < additionalSpaceCount; i++) {
			item = " " + item;
		}
		return item;
	}
	
	class ClearListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			clearReceivedText();
		}
	}

	private void clearReceivedText() {
		receivedText.setText("");
		countOfReadNumbers = 0;
	}

	class SendOnEnterPressListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				writeData();
			}
		}
	}
	
	class ChangeColumnCountListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			maxCountOfReadNumbers = Integer.parseInt(columnCountBox.getSelectedItem().toString());
			receivedText.setText("");
			countOfReadNumbers = 0;
		}
	}
	
	class SendBitsListener extends KeyAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			writeData();
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				writeData();
			}
		}
	}
	
	private void writeData() {
		String[] strNumbers = toSendTextArea.getText().trim().split(" ");
		if (strNumbers.length == 0) {
			JOptionPane.showMessageDialog(null, "Введіть числа для відправки");
		} else {
			if (bitSeqCheckbox.isSelected()) {
				for(String number: strNumbers) {
					boolean[] bits = ByteUtils.getBits(number);
					for(boolean bit: bits) {
						readerWriter.getWriter().write(bit);
					}
				}
			} else {
				for(String number: strNumbers) {
					readerWriter.writeByte(ByteUtils.getLastByteFromInt(Integer.parseInt(number)));
				}
			}
			readerWriter.flush();
			
			if (clearAfterSend.isSelected()) {
				toSendTextArea.setText("");
			}
		}
	}
	
	class StopTransferListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			readerWriter.reset();
		}
	}
	
	class ClearTransferListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			toSendTextArea.setText("");
		}
	}
	
	class SettingsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
		}
	}
	
	public void setReaderWriter(ReaderWriter readerWriter) {
		this.readerWriter = readerWriter;
		readerWriter.workInRawMode(rawModeBox.isSelected());
	}

	@Override
	public void byteReceived(byte b) {
		addByteToReceivedTextArea(Ints.fromBytes((byte) 0, (byte) 0, (byte) 0, b));
	}
	
	class RawModeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			readerWriter.workInRawMode(rawModeBox.isSelected());
		}
	}
	
	class CreatePacketListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//CreatePacketDialog dialog = new CreatePacketDialog();
			//dialog.setReceivePacketDialog(receivePacketDialog);
			///dialog.setReaderWriter(readerWriter);
			//FrameTools.situateOnCenter(dialog);
			//dialog.setVisible(true);
		}
	}
	
	class BinaryCheckboxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			receivedText.setText("");
			countOfReadNumbers = 0;
			
			if (receiveAsBinaryCheckbox.isSelected()) {
				maxCountOfReadNumbers /= 2;
			} else {
				maxCountOfReadNumbers *= 2;
			}
			
			columnCountBox.setSelectedItem(maxCountOfReadNumbers);
		}
	}
	
	class FindListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectionStart = receivedText.getText().indexOf(findText.getText());
			int selectionEnd = selectionStart + findText.getText().length();
			
			if (selectionStart > 0) {
				receivedText.select(selectionStart, selectionEnd);
			}
		}
	}
	
	class WindowClosingListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			needRun = false;
			if(readerWriter != null) {
				readerWriter.close();
			}
			dispose();
		}
	}
}
