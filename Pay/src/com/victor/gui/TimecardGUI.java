package com.victor.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.victor.actions.Action;
import com.victor.actions.Action.Event;
import com.victor.classes.TimeCard;
import com.victor.employees.Hourly;
import com.victor.main.Main;

public class TimecardGUI implements ActionListener {
	
	private JLabel result;
	private JFrame frame;
	private JPanel panel;
	private JFormattedTextField idField;
	private ButtonGroup timecardGroup;
	private JRadioButton arriveButton;
	private JRadioButton exitButton;
	private JButton createButton;
	
	public TimecardGUI() {
		frame = new JFrame("Create timecard");
		panel = new JPanel();
		
		result = new JLabel("");
		panel.add(result);
		
		panel.setBorder(BorderFactory.createEmptyBorder(5, 70, 20, 70));
		panel.setLayout(new GridLayout(0, 1));
		
		panel.add(result);
		
		JLabel removeLabel = new JLabel("Employee ID:");		
		idField = new JFormattedTextField();
		panel.add(removeLabel);
		panel.add(idField);
		
		timecardGroup = new ButtonGroup();
		arriveButton = new JRadioButton("Arrive");
		timecardGroup.add(arriveButton);
		exitButton = new JRadioButton("Exit");
		timecardGroup.add(exitButton);
		
		panel.add(arriveButton);
		panel.add(exitButton);
		
		createButton = new JButton("Create timecard");
		createButton.addActionListener(this);
		panel.add(createButton);
		
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.pack();
		
		frame.setVisible(true);
	}
	
	private void createTimecard() {
		try {
			if(idField.getText().isBlank()) {
				result.setText("The ID field cannot de blank!");
				return;
			}
			
			if(timecardGroup.getSelection() == null) {
				result.setText("The option is not selected!");
				return;
			}
			
			UUID id = UUID.fromString(idField.getText());
			if(Main.employees.containsKey(id)) {
				if(Main.employees.get(id) instanceof Hourly) {
					Hourly employee = (Hourly) Main.employees.get(id);
					if(arriveButton.isSelected()) {
						
						if(employee.getTimecards().isEmpty()) {
							employee.getTimecards().add(new TimeCard());
							JOptionPane.showMessageDialog(null, "Timecard for employee " + id + " has been created!", "Success!", JOptionPane.INFORMATION_MESSAGE);
							Main.lastAction = new Action(employee, null, null, null, Event.CREATE_TIMECARD);
							WindowEvent closingEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
							Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
						} else if(!employee.getTimecards().get(employee.getTimecards().size() - 1).isCompleted()) {
							result.setText("The last employee timecard is not completed!");
						} else {
							employee.getTimecards().add(new TimeCard());
							Main.lastAction = new Action(employee, null, null, null, Event.CREATE_TIMECARD);
							JOptionPane.showMessageDialog(null, "Timecard for employee " + id + " has been created!", "Success!", JOptionPane.INFORMATION_MESSAGE);
							WindowEvent closingEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
							Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
							employee.getTimecards().get(employee.getTimecards().size() - 1).print_info();
						}
						
					} else if(exitButton.isSelected()) {
						if(employee.getTimecards().isEmpty()) {
							result.setText("There are no open timecards!");
						} else if(!employee.getTimecards().get(employee.getTimecards().size() - 1).isCompleted()) {
							employee.getTimecards().get(employee.getTimecards().size() - 1).closeTimecard();
							Main.lastAction = new Action(employee, null, null, null, Event.CREATE_TIMECARD);
							JOptionPane.showMessageDialog(null, "Timecard for employee " + id + " has been updated!", "Success!", JOptionPane.INFORMATION_MESSAGE);
							WindowEvent closingEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
							Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
							
							employee.getTimecards().get(employee.getTimecards().size() - 1).print_info();
						} else {
							result.setText("There are no open timecards!");
						}
					} else {
						result.setText("Error while creating timecard for employee " + id);
					}
				} else {
					result.setText("This employee is not hourly!");
				}
			} else {
				result.setText("ID not founded!");
			}
		} catch (Exception e) {
			result.setText("This ID is not valid!");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == createButton) {
			createTimecard();
		}
	}

}
