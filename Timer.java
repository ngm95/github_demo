import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Timer extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public class TimeThread extends Thread  {
		JLabel timeLabel, printLabel, textLabel;
		JTextField tf;
		boolean change;
		boolean firstChange;
		public TimeThread(JLabel timeLabel, JTextField tf, JLabel printLabel, JLabel textLabel2) {
			super();
			this.timeLabel = timeLabel;
			this.tf = tf;
			this.printLabel = printLabel;
			this.textLabel = textLabel2;
			change = false;
			firstChange = false;
		}
		public void run() {
			String target = tf.getText(); 
			URL url = null;
			URLConnection conn = null;
			while(true) {
				String sHour = "", sMin = "", sSec = "";
				if (change) {
					target = tf.getText();
					if (!target.equals("Home") && (!target.contains("http://") && !target.contains("https://")))
						target = "http://" + target;
				}
				if (!firstChange || target.equals("Home")) {
					Calendar cal = Calendar.getInstance();
					int hour = cal.get(Calendar.HOUR_OF_DAY); 
					int min = cal.get(Calendar.MINUTE); 
					int sec = cal.get(Calendar.SECOND);
					sHour = Integer.toString(hour);
					if (sHour.length() < 2)
						sHour = "0" + sHour;
					sMin = Integer.toString(min);
					if (sMin.length() < 2)
						sMin = "0" + sMin;
					sSec = Integer.toString(sec);
					if (sSec.length() < 2)
						sSec = "0" + sSec;
					printLabel.setText(cal.getTime().toString());
					firstChange = false;
					change = false;
				}
				else {
					try {
						if (change) {
							url = new URL(target);
							change = false;
						}
						conn = url.openConnection();
						String date = conn.getHeaderField("Date");
						printLabel.setText(date);
						StringTokenizer st = new StringTokenizer(date, " ,:");
						String dayS = st.nextToken();
						String dayN = st.nextToken();
						String month = st.nextToken();
						String year = st.nextToken();
						sHour = Integer.toString((Integer.parseInt(st.nextToken()) + 9) % 24);
						if (sHour.length() < 2)
							sHour = "0" + sHour;
						sMin = st.nextToken();
						sSec = st.nextToken();
					}
					catch(IOException e) {
						timeLabel.setText("에러 발생. 프로그램 재부팅 요망");
					}
				}
				timeLabel.setText(sHour + " : " + sMin + " : " + sSec);
					
				try {
					Thread.sleep(1000);
				} catch(Exception e) {
					System.out.println("에러가 발생했습니다.");
					return;
				}
			}
		}
		public void changeTarget(JTextField tf) {
			this.tf = tf;
		}
		public void changeChange(boolean change) {
			this.change = change;
			firstChange = true;
		}
	}


	public Timer() {
		setTitle("서버 시간 알아보기");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(null);
		
		JLabel timeLabel = new JLabel();
		timeLabel.setFont(new Font("Gothic", Font.ITALIC, 80));
		timeLabel.setSize(16*27, 9*27);
		timeLabel.setLocation(10, 50);
		c.add(timeLabel);
		
		JLabel textLabel = new JLabel("URL : ");
		textLabel.setFont(new Font("Gothic", Font.ITALIC, 20));
		textLabel.setSize(70, 20);
		textLabel.setLocation(10, 10);
		c.add(textLabel);
		JTextField tf = new JTextField();
		tf.setLocation(0, 50);
		tf.setSize(310, 20);
		tf.setLocation(80, 10);
		c.add(tf);
		
		JLabel textLabel2 = new JLabel("컴퓨터 시간 출력 중");
		textLabel2.setFont(new Font("Gothic", Font.ITALIC, 20));
		textLabel2.setSize(350, 20);
		textLabel2.setLocation(10, 70);
		c.add(textLabel2);
		
		JLabel printLabel = new JLabel("");
		printLabel.setFont(new Font("Gothic", Font.ITALIC, 20));
		printLabel.setSize(350, 20);
		printLabel.setLocation(10, 100);
		c.add(printLabel);
		
		JLabel printLabel2 = new JLabel("Home입력시 컴퓨터 시간 표시");
		printLabel2.setFont(new Font("Gothic", Font.ITALIC, 18));
		printLabel2.setSize(500, 20);
		printLabel2.setLocation(10, 40);
		c.add(printLabel2);
		
		TimeThread tt = new TimeThread(timeLabel, tf, printLabel, textLabel2);
		
		JButton button = new JButton("Accept");
		button.setSize(80, 20);
		button.setLocation(395, 10);
		c.add(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tt.changeTarget(tf);
				tt.changeChange(true);
				if (tf.getText().equals("Home"))
					textLabel2.setText("컴퓨터 시간 출력 중");
				else
					textLabel2.setText(tf.getText() + " 서버시간 출력 중");
			}
		});
		
		setSize(16*31, 9*31);
		setVisible(true);
		
		
		tt.run();
	}
}
