
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    String getVoteContent(String s) {
    	
    	return s.split("/vote/")[1];
    }
    
    String getVoteTotal(String s) {
    	String[] sp = s.split(",");
    	String total = "";
    	for(int i=0; i<sp.length; i++) {
    		total += "0";
    		if(i < sp.length-1)
    			total+=",";
    	}
    	return total;
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
		StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        JSONObject json = new JSONObject(sb.toString());
        String content = json.getString("content");
        String date = json.getString("date_info");
        
        String type = request.getParameter("type");
        boolean vote = false;
        boolean addVote = false;
        boolean removeVote = false;
        if(type != null){
        	vote = type.equals("vote") ? true : false;
        	if(type.equals("add_vote"))
        		addVote=true;
        	if(type.equals("remove_vote")) {
        		removeVote = true;
        		
        		
        	}
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/test_for_chat", "root", "dltnqls11");

            if(!addVote) {
            	PreparedStatement ps = conn.prepareStatement("INSERT INTO chat_history (content, date_info) VALUES (?, ?)");
                ps.setString(1, content);
                ps.setString(2, date);
                ps.executeUpdate();

                ps.close();
                if(vote) {
                	
                	PreparedStatement ps2 = conn.prepareStatement("INSERT INTO vote (vote_content, date_info, vote_total) VALUES (?, ?, ?)");
                	ps2.setString(1, getVoteContent(content));
                	ps2.setString(2, date);
                	ps2.setString(3, getVoteTotal(getVoteContent(content)));
                	ps2.executeUpdate();
                	ps2.close();
                }
            }
            
            if(addVote) {
            	
            	String newVoteTotal = json.getString("vote_total");
            	String idx = json.getString("id");
            	String sql = "UPDATE vote SET vote_total = ? WHERE idx = ?";
            	PreparedStatement ps = conn.prepareStatement(sql);
            	ps.setString(1, newVoteTotal); // the new value you want to set
            	ps.setInt(2, Integer.parseInt(idx));             // the idx of the row to update
            	ps.executeUpdate();
            	ps.close();
            	
            }
            if(removeVote) {
            	int idx = json.getInt("id");
            	String sql = "DELETE FROM vote WHERE idx = ?";
            	PreparedStatement ps = conn.prepareStatement(sql);
            	ps.setInt(1, idx);
            	ps.executeUpdate();
            	ps.close();
            }
            
            
            conn.close();

            response.setStatus(200);
            PrintWriter out = response.getWriter();
            out.print("{\"result\": \"success\"}");
            out.flush();
        } catch (Exception e) {
            response.sendError(500, "DB error: " + e.getMessage());
        }
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=UTF-8");
	    JSONArray chatArray = new JSONArray();

	    String after = request.getParameter("after");
	    String sql;
	    String sql2;
	    String sql3 = "SELECT idx, vote_total FROM vote";
	    if (after != null && !after.isEmpty()) {
	        sql = "SELECT content, date_info FROM chat_history WHERE date_info > ? ORDER BY date_info ASC";
	        sql2 = "SELECT idx, vote_content, date_info, vote_total FROM vote WHERE date_info > ? ORDER BY date_info ASC";
	    } else {
	        sql = "SELECT content, date_info FROM chat_history ORDER BY date_info ASC";
	        sql2 = "SELECT idx, vote_content, date_info, vote_total FROM vote ORDER BY date_info ASC";
	    }

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        Connection conn = DriverManager.getConnection(
	            "jdbc:mysql://localhost:3306/test_for_chat", "root", "dltnqls11");

	        PreparedStatement ps = conn.prepareStatement(sql);
	        
	        PreparedStatement ps2 = conn.prepareStatement(sql2);
	        PreparedStatement ps3 = conn.prepareStatement(sql3);
	        
	        if (after != null && !after.isEmpty()) {
	            ps.setString(1, after);
	            ps2.setString(1, after);
	        }

	        ResultSet rs = ps.executeQuery();
	        
	        while (rs.next()) {
	            JSONObject chat = new JSONObject();
	            chat.put("content", rs.getString("content"));
	            chat.put("date_info", rs.getString("date_info"));
//	            chat.put("vote_content", rs2.getString("vote_content"));
//	            chat.put("vote_date_info", rs2.getString("date_info"));
	            chatArray.put(chat);
	        }

	        rs.close();
	        ps.close();
//	        
	        ResultSet rs2 = ps2.executeQuery();
	        while(rs2.next()) {
	        	JSONObject chat = new JSONObject();
	        	chat.put("vote_id", rs2.getString("idx"));
	        	chat.put("vote_content", rs2.getString("vote_content"));
	            chat.put("vote_date_info", rs2.getString("date_info"));
	            chat.put("vote_total", rs2.getString("vote_total"));
	            chatArray.put(chat);
	        }
	        
	        ResultSet rs3 = ps3.executeQuery();
	        JSONArray currentVoteIds = new JSONArray();
	        while(rs3.next()) {
	        	JSONObject chat = new JSONObject();
	        	chat.put("vote_refresh", "refresh");
	        	chat.put("vote_id", rs3.getString("idx"));
	            chat.put("vote_total", rs3.getString("vote_total"));
	            currentVoteIds.put(rs3.getString("idx"));
	            chatArray.put(chat);
	        }
	        JSONObject voteSync = new JSONObject();
	        voteSync.put("vote_sync", currentVoteIds);
	        chatArray.put(voteSync);
//	        
//	        
	        rs2.close();
	        ps2.close();
	        rs3.close();
	        ps3.close();
	        
	        conn.close();

	        PrintWriter out = response.getWriter();
	        out.print(chatArray.toString());
	        out.flush();
	    } catch (Exception e) {
	        response.sendError(500, "DB error: " + e.getMessage());
	    }
	}
}