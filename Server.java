import java.net.*;
import java.io.*;

public class Server extends Site implements Runnable {
	Socket socket = null;
	ServerSocket server = null;
	DataInputStream in = null;
	int port_num;
	int client_index;

	public Server() {
	}

	public Server(int port_num, int cli_index) {
		this.port_num = port_num;
		this.client_index = cli_index;
	}

	public String read_msg() {
		String res = "";
		try {
			if (in.available() > 0) {
				String line = (String) in.readUTF();
				
				String res_arr[] = line.split(":", 3); // Message has three parts: REQUEST:req_ts:site_id or
													   // REPLY:site_id:src_site

				if (res_arr[0].equals("REQUEST")) { // If message is REQUEST
					System.out.println("Received " + "REQUEST(" + res_arr[1] + ", " + res_arr[2] + ") " + "from Site: " + res_arr[2]);
					process_request(res_arr[1], res_arr[2]); // Process REQUEST
				} else if (res_arr[0].equals("REPLY")) { // If message is REPLY
					System.out.println("Received reply from Site: " + res_arr[2]);
					process_reply(res_arr[1]); // Process REPLY
				} else {
					System.out.println("Unknown message received!");
				}
			} else {
				Thread.sleep(5000);
			}
		} catch (Exception e) {
		}

		return res;
	}

	synchronized public void process_request(String req_ts_str, String site_id_str) {
		int req_ts = Integer.parseInt(req_ts_str);
		int site_id = Integer.parseInt(site_id_str);

		if (Site.req_cs_entry) {
			if (req_ts < Site.curr_req_ts) {
				// Send reply to the site_id
				Site.cliObj.get(client_index).send_reply(Site.site_id, site_id);

				if (Site.prev_req_ts < req_ts)
					Site.prev_req_ts = req_ts; // To make channels fifo (causal order)
			} else if (req_ts > Site.curr_req_ts) {
				// Defer reply to the site_id
				Site.def_list.set(site_id, 1);
				Site.req_msg_list.set(site_id, req_ts);

				if (Site.prev_req_ts < req_ts)
					Site.prev_req_ts = req_ts; // To make channels fifo (causal order)

				System.out.println("Request from Site: " + site_id + " is deferred. Request ts: " + req_ts);
				Site.print_request_deferred_array();
			} else {
				if (site_id < Site.site_id) {
					// Send reply to the site_id
					Site.cliObj.get(client_index).send_reply(Site.site_id, site_id);

					if (Site.prev_req_ts < req_ts)
						Site.prev_req_ts = req_ts; // To make channels fifo (causal order)
				} else {
					// Defer reply to the site_id
					Site.def_list.set(site_id, 1);
					Site.req_msg_list.set(site_id, req_ts);

					if (Site.prev_req_ts < req_ts)
						Site.prev_req_ts = req_ts; // To make channels fifo (causal order)

					System.out.println("Request from Site: " + site_id + " is deferred. Request ts: " + req_ts);
					Site.print_request_deferred_array();
				}
			}
		} else { // 
			// Send reply to the site_id
			Site.cliObj.get(client_index).send_reply(Site.site_id, site_id);
			if (Site.prev_req_ts < req_ts)
				Site.prev_req_ts = req_ts; // To make channels fifo (causal order)
		}
	}

	synchronized public void process_reply(String site_id_str) {
		try {
			num_of_pending_sites--;

			if (num_of_pending_sites == 0) {
				enter_cs = true;

				Thread.sleep(10);
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
	}

	public void run() {
		try {
			System.out.println("Server Listening @" + port_num);
			server = new ServerSocket(port_num);
			Thread.sleep(10);
			socket = server.accept();
			in = new DataInputStream(socket.getInputStream());
			System.out.println("Connection Accepted @" + port_num);
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}

		while (true) {
			try {
				read_msg();
				Thread.sleep(10);
			} catch (Exception e) {
				System.out.println("Exception:" + e);

			}
		}
	}
}
