import java.io.Serializable;
import java.util.List;


public class Token implements UserToken, Serializable
{
	private String issuer;
	private String subject;
	private String serverId;
	private List<String> groups;
	
	public Token(String i, String s, String sid, List<String> g)
	{
		issuer = i;
		subject = s;

		serverId = null;

		groups = g;
	}
	
	
	/**
     * This method should return a string describing the issuer of
     * this token.  This string identifies the group server that
     * created this token.  For instance, if "Alice" requests a token
     * from the group server "Server1", this method will return the
     * string "Server1".
     *
     * @return The issuer of this token
     *
     */
	public String getIssuer()
	{
		return issuer;
	}

    /**
     * This method should return a string indicating the name of the
     * subject of the token.  For instance, if "Alice" requests a
     * token from the group server "Server1", this method will return
     * the string "Alice".
     *
     * @return The subject of this token
     *
     */
    public String getSubject()
    {
    	return subject;
    }


    /**
     * This method extracts the list of groups that the owner of this
     * token has access to.  If "Alice" is a member of the groups "G1"
     * and "G2" defined at the group server "Server1", this method
     * will return ["G1", "G2"].
     *
     * @return The list of group memberships encoded in this token
     *
     */
    public List<String> getGroups()
    {
    	return groups;
    }
    
    
    /**
     * This method extracts the intended IP address of the server
     * where the token is intended to be used.
     * For example, if user "Alice" requests a token to be used for
     * file server "fs" at IP Address "11037", this method will 
     * return the String "11037".
     * 
     * @return the String representation of the intended server's IP address.
     *
     */
     public String getServerID() 
     {
     	return serverId;
     }
    
    public void setServerID(String s)
    {
    	serverId = s;
    }
    
    public byte[] getBytes()
    {
    	String[] tokenInfo = new String[4];
		tokenInfo[0] = issuer + ",";
		tokenInfo[1] = subject + ",";
		tokenInfo[2] = serverId + ",";
		String groupStr = "";
		for (String x:groups){
			groupStr = groupStr + "," + x;
		}
		tokenInfo[2] = groupStr;
		byte[] tokenBytes = new byte[tokenInfo.length];
		int i = 0;
		for (String s: tokenInfo) {
			tokenBytes[i] = Byte.parseByte(s);
			i++;
		}
		return tokenBytes;
    }

}
