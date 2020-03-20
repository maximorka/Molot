package com.molot.lowlevel.rw.ether;

import com.molot.lowlevel.rw.ether.tester.EthernetTestListener;
import com.molot.lowlevel.rw.ether.tester.EthernetTester;
import com.molot.util.Params;
import javafx.application.Platform;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class EthernetDialog extends JDialog {
	private static final long serialVersionUID = 7475437276936227364L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField ipText;
	private JButton connectButton;
	private JButton testButton;
	private JLabel label;
	private JTextField portText;
	private JLabel youIpAddress;
	
	public static void main(String[] args) {
		try {
			EthernetDialog dialog = new EthernetDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EthernetDialog() {
		setTitle("Налаштування Ethernet");
		setBounds(100, 100, 350, 168);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JLabel lblIp = new JLabel("IP:");
		
		ipText = new JTextField();
		ipText.setText(getIpFromSettings());
		ipText.setColumns(10);
		label = new JLabel("Порт:");
		portText = new JTextField();
		portText.setText(getPortFromSettings());
		portText.setColumns(10);
		
		youIpAddress = new JLabel("Ваша IP-адреса:192.168.0.10");
		youIpAddress.setForeground(Color.LIGHT_GRAY);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblIp)
						.addComponent(label))
					.addGap(22)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(portText, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
						.addComponent(ipText, GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)))
				.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(youIpAddress))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblIp)
						.addComponent(ipText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(portText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
					.addComponent(youIpAddress))
		);
		
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			{
				testButton = new JButton("Тест");
				testButton.addActionListener(new TestListener());
				buttonPane.add(testButton);
			}
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				buttonPane.add(horizontalGlue);
			}
			{
				connectButton = new JButton("Підключитись");
				connectButton.addActionListener(new ConnectListener());
				buttonPane.add(connectButton);
			}
		}
		
		youIpAddress.setText("Ваша IP адреса: " + getCurrentIpAddress());
	}
	
	private void setButtonsEnabled(final boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				connectButton.setEnabled(enabled);
				testButton.setEnabled(enabled);
			}
		});
	}
	
	class TestListener implements ActionListener, EthernetTestListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			setButtonsEnabled(false);
			new EthernetTester(this).test(ipText.getText(), Integer.parseInt(portText.getText()));
		}

		@Override
		public void success() {
			showMessage("Підключення успішне!");

			saveEthernetParams();
			setButtonsEnabled(true);
		}

		@Override
		public void fail() {
			setButtonsEnabled(true);
			showMessage("Помилка - заданий вузол недоступний!");
		}
	}
	
	class ConnectListener implements ActionListener, EthernetTestListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setButtonsEnabled(false);
			
			new EthernetTester(this).test(ipText.getText(), Integer.parseInt(portText.getText()));
		}

		@Override
		public void success() {
			saveEthernetParams();
			Platform.runLater(() -> {
						Params.ETHERNET.putBoolean("present", true);
						Params.ETHERNET.putString("ip", ipText.getText());
						Params.ETHERNET.putString("port", portText.getText());
					});

		}

		@Override
		public void fail() {
			setButtonsEnabled(true);
			showMessage("Помилка підключення, перевірте правильність IP адреси");
		}
	}
	
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(null, message);
			}
		});
	}
	
	private String getIpFromSettings() {
		return Params.SETTINGS.getString("ethernet-ip-address");
	}
	
	private String getPortFromSettings() {
		return Params.SETTINGS.getString("ethernet-port");
	}
	
	private void saveEthernetParams() {
		Params.SETTINGS.putString("ethernet-ip-address", ipText.getText());
		Params.SETTINGS.putString("ethernet-port", portText.getText());
		Params.SETTINGS.save();
	}
	
	private String getCurrentIpAddress() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while(interfaces.hasMoreElements()) {
				NetworkInterface i = interfaces.nextElement();
				if (i.getName().contains("eth")) {
					Enumeration<InetAddress> addresses = i.getInetAddresses();
					while(addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (address.getHostAddress().length() <= 15) {
							return address.getHostAddress();
						}
					}
//					return i.getInetAddresses().nextElement().getHostAddress();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	
		return "...";
	}
	public JLabel getYouIpAddress() {
		return youIpAddress;
	}
}
