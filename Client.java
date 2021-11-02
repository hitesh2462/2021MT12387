import java.net.*;
import java.io.*;

public class Client extends Site {

	Socket client_socket;
	DataOutputStream client_dout;

	int port_num;

	public Client(int port_num) {
		this.port_num = port_num;
	}

	public void connect(int port_num) {
		try {
			client_socket = new Socket("localhost", port_num);
			client_dout = new DataOutputStream(client_socket.getOutputStream());
		} catch (Exception i) {
			System.out.println(i);
		}
	}

	public void send_request(int dest_site, int req_ts, int site_id) {

		try {
			client_dout.writeUTF("REQUEST:" + Integer.toString(req_ts) + ":" + Integer.toString(site_id)); // Request
																											// message
																											// to
																											// dest_site
			System.out.println("Sent " + "REQUEST(" + req_ts + ", " + site_id + ") " + "to Site: " + dest_site);
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
	}

	public void send_reply(int src_site, int site_id) {
		try {
			client_dout.writeUTF("REPLY:" + Integer.toString(site_id) + ":" + Integer.toString(src_site)); // Reply message to site_id
			System.out.println("Sent reply to Site: " + site_id);
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
	}
}
