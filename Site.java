import java.util.*;
import java.io.*;
import java.sql.Timestamp;

public class Site {
	static int site_id;
	static boolean enter_cs;
	static int max_sites;
	static int curr_req_ts; // Current request timestamp
	static int prev_req_ts; // Previous request timestamp
	static public int num_of_pending_sites;
	static boolean req_cs_entry;
	int num_of_times_enter_cs;

	List<Integer> port_num = new ArrayList<Integer>();

	static List<Integer> def_list = new ArrayList<Integer>();
	static List<Integer> req_msg_list = new ArrayList<Integer>();
	static List<Client> cliObj = new ArrayList<Client>();
	static List<Server> serObj = new ArrayList<Server>();
	static List<Thread> serThreads = new ArrayList<Thread>();

	Timestamp ts;

	public Site(int id, int ctr) {
		site_id = id;
		enter_cs = false;
		curr_req_ts = 0;
		prev_req_ts = 0;
		num_of_pending_sites = 0;
		req_cs_entry = false;
		this.num_of_times_enter_cs = ctr;
	}

	public Site() {
	}

	public void read_config_file() {
		try {
			File file = new File("config.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			String[] words;
			int site_num;
			boolean firstentry = true;

			while ((str = br.readLine()) != null) {
				if (firstentry) {
					max_sites = Integer.parseInt(str);
					System.out.println("Max number of Sites in the current setup: " + max_sites);
					firstentry = false;
				} else {
					words = str.split(" ");
					site_num = Integer.parseInt(words[0]);
					port_num.add(site_num, Integer.parseInt(words[1]));
					def_list.add(site_num, 0); // Deferred array of current site initialized as 0 for max_sites
					req_msg_list.add(site_num, 0); // All sites' req_ts is initialized as 0 
				}
			}
			br.close();
			print_request_deferred_array();
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
	}

	public void cs_requisite() {
		curr_req_ts = prev_req_ts + 1;
		req_cs_entry = true;
		num_of_pending_sites = max_sites - 1;

		for (int i = 0; i < max_sites; i++) {
			if (i == site_id)
				continue;
			cliObj.get(i).send_request(i, curr_req_ts, site_id);
		}
	}

	public void process_deferred_requests() {
		for (int i = 0; i < def_list.size(); i++) {				
			if (def_list.get(i) == 1) { // If deferred request pending
				String site_id_str = Integer.toString(i);
				String req_ts_str = Integer.toString(req_msg_list.get(i));
				serObj.get(i).process_request(req_ts_str, site_id_str);
				def_list.set(i, 0);
			}
		}
	}

	public static void print_request_deferred_array() {
		for (int i = 0; i < def_list.size(); i++) {
			if (i == 0)
				System.out.print("RD" + site_id + " = [" + def_list.get(i) + ", ");
			else if (i < max_sites - 1)
				System.out.print(def_list.get(i) + ", ");
			else
				System.out.println(def_list.get(i) + "]");
		}
	}

	public void Site_main() { // Site's main process

		try {
			read_config_file();

			for (int i = 0; i < max_sites; i++) {
				// Server component of each site
				if (i == site_id) {
					serObj.add(i, new Server(0, i));
					Thread t1 = new Thread(serObj.get(i));
					serThreads.add(i, t1);
				} else {
					int temp_port_num = port_num.get(i);
					serObj.add(i, new Server(temp_port_num + site_id, i));
					Thread t1 = new Thread(serObj.get(i));
					serThreads.add(i, t1);
					t1.start();
				}
			}

			Thread.sleep(10000);

			for (int i = 0; i < max_sites; i++) {
				// Client component of each site
				if (i == site_id) {
					int tmp_port_no = port_num.get(i);
					for (int j = 0; j < max_sites; j++) {
						if (j == site_id) {
							cliObj.add(j, new Client(tmp_port_no + j));
						} else {
							cliObj.add(j, new Client(tmp_port_no + j));
							cliObj.get(j).connect(tmp_port_no + j);
						}
					}
				}
			}

			for (int i = 0; i < num_of_times_enter_cs; i++) {
				Thread.sleep(10000);

				cs_requisite(); // Steps to be followed before entering CS

				while (enter_cs == false) {
					Thread.sleep(10);
				}
				// Enter critical section
				ts = new Timestamp(System.currentTimeMillis());
				System.out.println("Site: " + site_id + " entered Critical Section @ " + ts);
				Thread.sleep(2000);

				// Exit critical section & reset variables
				enter_cs = false;
				req_cs_entry = false;
				ts = new Timestamp(System.currentTimeMillis());
				System.out.println("Site: " + site_id + " exited Critical Section @ " + ts);

				print_request_deferred_array(); // Before processing deferred requests
				process_deferred_requests();
				print_request_deferred_array(); // After processing deferred requests
			}

			Thread.sleep(10000);

			for (int i = 0; i < max_sites; i++) {
				if (i != site_id) {
					serObj.get(i).server.close();
				}
			}
			System.out.println("Site: " + site_id + " entered CS " + num_of_times_enter_cs + " times");
			System.out.println("Site: " + site_id + " completed execution");
			System.exit(0);

		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
	}
}
