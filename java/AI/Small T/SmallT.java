/*版本更新及说明

2016/7/14
v1.1:第一个版本。

2016/7/15
v1.2:使得一种问题能有多种答案，小T会随机抽取答案。

*/

import java.util.*;
import java.io.*;

class StrLenComparator implements Comparator<String>
{
	public int compare(String s1,String s2)
	{
		if(s1.length()>s2.length())
			return 1;
		if(s1.length()<s2.length())
			return -1;
		return s1.compareTo(s2);
	}
}

class Smart//Smart只是为了好听，其实是Tool
{
	//匹配中文的正则表达式。
	public static final String CN = "[\u4e00-\u9fa5]";
	//对话库文件名
	public static final String DIALOG = "dialog.nb";
	//安全对话库文件名。2016/7/15添加。
	public static final String SAFE_DIALOG = "dialog.znb";
	//QA分隔符。
	public static final String FGF = "fgf007";
	//答案分隔符。
	public static final String FGF_A = "fgf009";
	//小T说话面板。
	public static final String TS = "Small T:\n\t";
	//用户说话面板。
	public static final String YS = "You:\n\t";
	//返回随机整数。
	public int rangeRand(int from,int to)
	{
		Random r = new Random();
		return r.nextInt(to-from)+from;
	}
	//读取用户录入。
	public String readIn()
	{
		//InputStream in = System.in;//直接这么用，读不了中文。
		BufferedReader in;
		//int ch = 0;StringBuilder sb = new StringBuilder();
		try
		{
			in = new BufferedReader(new InputStreamReader(System.in));
			/*
			while((ch=in.read())!=-1)//read()是阻塞式方法，当没有读到数据时，就会等待。
			{
				if(ch == '\r')
					continue;
				if(ch == '\n')
					return sb.toString();
				sb.append((char)ch);
			}
			*/
			return in.readLine();
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		return null;
	}
	//装载TreeMap。
	public void loadTm(TreeMap<String,String> tm,String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String str = null,key,value;
		String kv[];
		while((str=br.readLine())!=null)
		{
			kv = str.split(FGF);
			key = kv[0];value = kv[1];
			tm.put(key,value);
		}
		br.close();
	}
	//简化，使字符串只含中文
	public String simplyWords(String words)
	{
		StringBuilder sb = new StringBuilder();
		char ch;
		for(int i=0;i<words.length();i++)
		{
			ch = words.charAt(i);
			if(String.valueOf(ch).matches(CN))
				sb.append(ch);
		}
		return sb.toString();
	}
	//添加并保存数据。
	public void saveData(String key,String value) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(DIALOG,true));
		String data = key + FGF + value;
		bw.newLine();
		bw.write(data);
		bw.close();
	}
	//修改指定key的值并保存数据。
	//2016/7/15完工。
	public void saveData(TreeMap<String,String> tm,String key,String value) throws IOException
	{
		tm.put(key,value);
		copyFile(DIALOG,SAFE_DIALOG);
		BufferedWriter bw = new BufferedWriter(new FileWriter(DIALOG));//没加true，是覆盖，有安全隐患!!!
		boolean flag =false;
		for(String str : tm.keySet())
		{
			if(flag)//第一次不换行。
				bw.newLine();
			if(!flag)
				flag = true;
			bw.write(str+FGF+tm.get(str));	
			bw.flush();
		}
		bw.close();
		//bw.close()后文件成功完全覆盖，需要删除安全文件，如果安全文件存在，则说明极有可能对话库出了问题。
		deleteFile(SAFE_DIALOG);
	}
	//复制文件，如果复制失败，新文件无内容，成功则已完全复制。2016/7/15添加。
	public void copyFile(String oldFileName,String newFileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(oldFileName));
		BufferedWriter bw = new BufferedWriter(new FileWriter(newFileName));
		boolean flag = false;
		String data = null;
		while((data=br.readLine())!=null)
		{
			if(flag)//第一次不换行。
				bw.newLine();
			if(!flag)
				flag = true;
			bw.write(data);//不刷新，这样一来，如果在复制过程出问题，新文件无内容。
		}
		br.close();
		bw.close();//刷新，此时已经复制完毕，新文件完全复制了旧文件。
	}
	//删除文件。2016/7/15添加。
	public void deleteFile(String fileName)
	{
		File f = new File(fileName);
		if(f.isFile() && f.exists())
			f.delete(); 
	}
	//被教学。
	public String teached()
	{
		String question,answer;
		System.out.print("好啊~\n======教学时间======\n"+TS+"问题是什么呢？\n"+YS);
		question = simplyWords(readIn());
		System.out.print(TS+"我该怎么回答呢~\n"+YS);
		answer = readIn();
		System.out.print("嗯嗯，知道啦~\n======教学结束======\n");
		return question + FGF + answer;
	}
	//休眠进程。2016/7/15添加。
	public void sleep(int time)
	{
		try{Thread.sleep(time);}catch(Exception e){System.out.println("休眠异常");}
	}
}

class SmallT extends Smart
{
	private int perWordTime = 100;
	private TreeMap<String,String> tm;
	SmallT()
	{
		//初始化对话库。
		initTm();
		//System.out.println(tm);
	}
	SmallT(int perWordTime)
	{
		this.perWordTime = perWordTime;
		//初始化对话库。
		initTm();
	}
	//初始化对话库。
	private void initTm()
	{
		//对话库从长到短排列。
		tm = new TreeMap<String,String>(Collections.reverseOrder(new StrLenComparator()));
		//加载对话库。
		
		try
		{
			loadTm(tm,DIALOG);	
		}
		catch (IOException e)
		{
			System.out.println("装载对话库失败！");
		}
	}
	//聊天，建立在answer的基础上。
	public void chat()
	{
		String words;
		System.out.print(YS);
		while(!((words=readIn()).equals("over")))
		{
			//精简。
			words = simplyWords(words);
			//教学。
			if(words.contains("教你"))
			{
				System.out.print(TS);
				study(teached());
				System.out.print(YS);
			}
			else
			{
				String reply = answer(words);
				sleep(reply.length()*perWordTime);
				//聊天面板。
				System.out.print(TS+reply+"\n"+YS);
			}
				
		}
			
	}
	//回答，读取数据后，对数据最表面层的操作。
	//2016/7/15更新：对同一个问题可以有多种回答，小T会从中随机抽取答案。
	private String answer(String words)
	{
		//寻找答案。
		words = findAnswer(words);
		//如果没找到。
		if(words == null)
			return "没听明白，，，";
		//如果有多个答案，随机返回其中一个。
		if(words.contains(FGF_A))
		{
			String[] word = words.split(FGF_A);
			int to = word.length;
			int pos = rangeRand(0,to);
			words = word[pos];
		}
		return words;
	}
	//寻找答案，没找到答案时返回null。
	private String findAnswer(String words)
	{
		for(String key : tm.keySet())
		{
			//System.out.println(key);
			if(words.contains(key))
				return tm.get(key);
		}
		return null;
	}
	//学习，小T唯一的更新数据的方法。
	//2016/7/15更新：以前只能增加问题（就算问题已存在），现在能增加已有问题的答案。
	private void study(String question,String answer)
	{
		//如果问题已存在。
		String oldAnswer = null;
		if((oldAnswer=tm.get(question))!=null)
			answer = oldAnswer + FGF_A + answer;
		String oldKey = tm.put(question,answer);
		//保存数据。
		try
		{
			if(oldKey == null)
				//添加数据。
				saveData(question,answer);
			else
				//修改数据。
				saveData(tm,question,answer);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}	
	}
	private void study(String content)
	{
		String[] qa = content.split(FGF);
		String question = qa[0];
		String answer = qa[1];
		study(question,answer);
	}
	
}
