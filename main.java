import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import java.util.*;
/*
worst,custom
empty value chosen
number of processes more than number of holes
which process waits
visuals-labels,colors,alignment
*/
class Fits implements ActionListener
{
	static int n,h;
    JFrame f = new JFrame("Fitness");
    JLabel jlp[] = new JLabel[n];
    JTextField jtp[] = new JTextField[n];
	static int useless[]=new int[3];
	static int pos[][];
	static int prog[][];
    JLabel jlh[] = new JLabel[h];
    JTextField jth[] = new JTextField[h];
	
	public Fits(int n,int h)
	{
		for(int i=0;i<n;i++)
		{
			jlp[i] = new JLabel("Process "+(i+1)+": ");
			jlp[i].setBounds(50,50+(35*i),100,25);
			f.add(jlp[i]);

			jtp[i] = new HintTextField("Enter Process size");
			jtp[i].setBounds(150,50+(35*i),200,25);
			f.add(jtp[i]);
		}
		for(int i=0;i<h;i++)
		{
			jlh[i] = new JLabel("Block "+(i+1)+": ");
			jlh[i].setBounds(500,50+(35*i),80,25);
			f.add(jlh[i]);

			jth[i] = new HintTextField("Enter Block size");
			jth[i].setBounds(600,50+(35*i),200,25);
			f.add(jth[i]);
		}
		JButton calculate=new JButton("Fit it");
		calculate.setBounds(380,350,100,20);
		calculate.addActionListener(this);
		f.add(calculate);
		JButton cancel=new JButton("Cancel");
		cancel.setBounds(520,350,100,20);
		f.add(cancel);
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				f.dispose();
			}
		});
		JLabel kk = new JLabel("");
		f.add(kk);
		f.setSize(1000,600);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e)
	{
		JFrame j=new JFrame("Custom input");
		JLabel jlh2[] = new JLabel[h];
		String[] pf = new String[n+1];
		JComboBox jcp[]= new JComboBox[h];
		int selection[]=new int[h];
		for(int i=0;i<n;i++)
		{
			pf[i]=("Process "+(i+1)+": "+jtp[i].getText().toString()+" kb");
		}
		pf[n]="Empty";
		for(int i=0;i<h;i++)
		{
			jlh2[i] = new JLabel("Block "+(i+1)+" ("+jth[i].getText().toString()+" kb):");
			jlh2[i].setBounds(50,50+(35*i),150,25);
			jcp[i]=	new JComboBox(pf);
			jcp[i].setBounds(150,50+(35*i),200,25);
			j.add(jcp[i]);
			j.add(jlh2[i]);                   
				
		}
		JButton calculate=new JButton("Check");
		calculate.setBounds(380,350,100,20);
		calculate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame fin=new JFrame("Final");
				JProgressBar jpb[][]=new JProgressBar[h][3];
				
				boolean flag=false;
				for(int i=0;i<h;i++)
				{
					flag=false;
					selection[i]=jcp[i].getSelectedIndex();
					if(selection[i]!=n&&Integer.parseInt(jth[i].getText().toString())<Integer.parseInt(jtp[selection[i]].getText().toString()))
					{
						JOptionPane.showMessageDialog(new JDialog(),"Process "+(selection[i]+1)+" is greater than the block size");
						flag=true;
					}
					else{
					for(int j=0;j<i;j++)
					{
						if(selection[i]!=n&&selection[i]==selection[j])
						{
							JOptionPane.showMessageDialog(new JDialog(),"Process "+(selection[i]+1)+" cannot be allocated to multiple holes");
							flag=true;
						}
					}
					}
					if(flag)
					{
						fin.dispose();
						break;
					}
					
				}
				if(!flag)
				{
					int s[]=new int[h];
					int s1[]=new int[n];
					int custom[]=new int[h];
					for(int i=0;i<h;i++)
					{
						s[i]=Integer.parseInt(jth[i].getText().toString().trim());
					}
					for(int i=0;i<n;i++)
					{
						s1[i]=Integer.parseInt(jtp[i].getText().toString().trim());
					}
					for(int i=0;i<h;i++)
					{
						if(selection[i]!=n)
							custom[i]=Integer.parseInt(jtp[selection[i]].getText().toString());
						else
							custom[i]=0;
					}
					
					int besta[]=Fits.bf(s,s1,h,n);
					int worsta[]=Fits.wf(s,s1,h,n);
					int customa[]=Fits.custom(s,custom,h,n,s1);
					
					//Holes
					JLabel jlhf_b[] = new JLabel[h];
					JLabel jlhf_c[] = new JLabel[h];
					JLabel jlhf_w[] = new JLabel[h];
					
					//Processes
					JLabel jlpf_b[] = new JLabel[h];
					JLabel jlpf_c[] = new JLabel[h];
					JLabel jlpf_w[] = new JLabel[h];
					for(int i=0;i<h;i++)
					{
						//Best
						jlhf_b[i] = new JLabel("Block "+(i+1)+" ("+jth[i].getText().toString()+" kb):");
						jlhf_b[i].setBounds(50,50+(35*i),150,25);
						
						
						jpb[i][0]=new JProgressBar(0,100);
						jpb[i][0].setValue(prog[i][0]);
						jpb[i][0].setBounds(150,50+(35*i),200,25);
						jpb[i][0].setString(Integer.toString(prog[i][0])+"%");
						jpb[i][0].setStringPainted(true);
						
						if(besta[i]!=-1)
							jlpf_b[i] = new JLabel("Process "+(besta[i]+1)+" ("+jtp[besta[i]].getText().toString()+" kb):");
						else
							jlpf_b[i] = new JLabel("Empty");
						jlpf_b[i].setBounds(350,50+(35*i),150,25);
						
						//Custom
						jlhf_c[i] = new JLabel("Block "+(i+1)+" ("+jth[i].getText().toString()+" kb):");
						jlhf_c[i].setBounds(600,50+(35*i),150,25);
						
						jpb[i][1]=new JProgressBar(0,100);
						jpb[i][1].setValue(prog[i][1]);
						jpb[i][1].setBounds(700,50+(35*i),200,25);
						jpb[i][1].setString(Integer.toString(prog[i][1])+"%");
						jpb[i][1].setStringPainted(true);
						
						
						if(customa[i]!=-1)
							jlpf_c[i] = new JLabel("Process "+(customa[i]+1)+" ("+jtp[customa[i]].getText().toString()+" kb):");
						else
							jlpf_c[i] = new JLabel("Empty");
						jlpf_c[i].setBounds(900,50+(35*i),150,25);
						
						//Worst
						jlhf_w[i] = new JLabel("Block "+(i+1)+" ("+jth[i].getText().toString()+" kb):");
						jlhf_w[i].setBounds(1150,50+(35*i),150,25);
						
						jpb[i][2]=new JProgressBar(0,100);
						jpb[i][2].setValue(prog[i][2]);
						jpb[i][2].setBounds(1300,50+(35*i),200,25);
						jpb[i][2].setString(Integer.toString(prog[i][2])+"%");
						jpb[i][2].setStringPainted(true);
						
						if(worsta[i]!=-1)
							jlpf_w[i] = new JLabel("Process "+(worsta[i]+1)+" ("+jtp[worsta[i]].getText().toString()+" kb):");
						else
							jlpf_w[i] = new JLabel("Empty");
						jlpf_w[i].setBounds(1500,50+(35*i),150,25);
						
						
						fin.add(jlhf_b[i]);
						fin.add(jlhf_c[i]);
						fin.add(jlhf_w[i]);
						fin.add(jlpf_b[i]);
						fin.add(jlpf_c[i]);
						fin.add(jlpf_w[i]);
						fin.add(jpb[i][0]);	
						fin.add(jpb[i][1]);	
						fin.add(jpb[i][2]);							
					}
					
					JLabel kkk = new JLabel("");
					fin.add(kkk);	
					fin.setSize(1920,600);
					fin.setResizable(false);
					fin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					fin.setVisible(true);
				}
				
				
				
			}
		});
			j.add(calculate);
			JButton cancel=new JButton("Cancel");
			cancel.setBounds(520,350,100,20);
			j.add(cancel);
			
			cancel.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					j.dispose();
				}
			});
		
			
		JLabel kk = new JLabel("");
		j.add(kk);	
		j.setSize(1000,600);
		j.setResizable(false);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		j.setVisible(true);
	}
	

	final static int[] bf(int s2[],int s1[],int p,int n)
	{
		//s2 is the hole size, p=number of holes
		//s1 process size,n=no of processes
		int[] s = Arrays.copyOf(s2, s2.length); 
		int[] og= Arrays.copyOf(s2, s2.length);
		Arrays.sort(s);
		useless[0]=0;
		int total=0;
		for(int i=0;i<p;i++)
			total+=s[i];
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<p;j++)
			{
				if(s[j]>=s1[i])
				{
					int temp=Fits.search(og,s[j]);
					prog[temp][0]=(int)Math.round((s1[i]*100)/s[j]);
					useless[0]+=og[temp];
					og[temp]=i;
					s[j]=0;
					break;
				}
			}
		}
		useless[0]=((total-useless[0])*100/total);
		return og;
	}
	final static int[] wf(int s2[],int s1[],int p,int n)
	{
		//s2 is the hole size, p=number of holes
		//s1 process size,n=no of processes
		int[] s = Arrays.copyOf(s2, s2.length); 
		int[] og= Arrays.copyOf(s2, s2.length);
		boolean[] filled=new boolean [p];
		Arrays.sort(s);
		useless[2]=0;
		int total=0;
		for(int i=0;i<s.length;i++)
			total+=s[i];
		
		
		for(int i=0;i<n;i++)
		{
			for(int j=p-1;j>=0;j--)
			{
				if(s[j]>=s1[i])
				{
					int temp=Fits.search(og,s[j]);
					prog[temp][2]=(int)Math.round((s1[i]*100)/s[j]);
					useless[2]+=og[temp];
					og[temp]=i;
					filled[temp]=true;
					s[j]=0;
					break;
				}
			}
		}
		for(int i=0;i<p;i++)
		{
			if(!filled[i])
				og[i]=-1;
		}
		useless[2]=((total-useless[2])*100/total);
		return og;
	}
	final static int[] custom(int s2[],int custom[],int p,int n,int s1[])
	{
		//s is the hole size, p=number of holes
		//s1 process size,n=no of processes
		int[] s = Arrays.copyOf(s2, s2.length); 
		int[] og= Arrays.copyOf(s2, s2.length);
		useless[1]=0;
		int total=0;
		for(int i=0;i<h;i++)
			total+=s[i];
		for(int i=0;i<h;i++)
		{
			int temp=Fits.search(s1,custom[i]);
			prog[i][1]=(int)Math.round((custom[i]*100)/s[i]);
			useless[1]+=custom[i];
			og[i]=temp;
			s[i]=0;
		}
		useless[1]=((total-useless[1])*100)/total;
		return og;
		
	}
	
	final static int search(int[] a,int x)
	{
		for(int i=0;i<a.length;i++)
		{
			if(a[i]==x)
				return i;
		}
		return -1;
	}
	
	public static void main(String args[])
	{
		n = Integer.parseInt(JOptionPane.showInputDialog("Enter no. of processes: "));
		h = Integer.parseInt(JOptionPane.showInputDialog("Enter no. of holes in main memory: "));
		pos=new int[h][3];
		prog=new int[h][3];
		if (n>h)
				JOptionPane.showMessageDialog(new JDialog(),"More processes than holes, some processes may not be allocated.");
		new Fits(n,h);
	}
}

class HintTextField extends JTextField implements FocusListener 
{
	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
    super(hint);
    this.hint = hint;
    this.showingHint = true;
    super.addFocusListener(this);
}

  @Override
  public void focusGained(FocusEvent e) {
    if(this.getText().isEmpty()) {
      super.setText("");
      showingHint = false;
    }
  }
  @Override
  public void focusLost(FocusEvent e) {
    if(this.getText().isEmpty()) {
      super.setText(hint);
      showingHint = true;
    }
  }

  @Override
  public String getText() {
    return showingHint ? "" : super.getText();
  }
}