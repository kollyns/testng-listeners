 /*
  * The contents of this file are subject to the Mozilla Public
  * License Version 1.1 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.mozilla.org/MPL/
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * The Original Code is the Bugzilla Testopia Java API.
  *
  * The Initial Developer of the Original Code is Andrew Nelson.
  * Portions created by Andrew Nelson are Copyright (C) 2006
  * Novell. All Rights Reserved.
  *
  * Contributor(s): Andrew Nelson <anelson@novell.com>
  *					Jason Sabin <jsabin@novell.com>
  */
package testopia.API;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpState;
import org.apache.xmlrpc.client.XmlRpcClient;

/**
 * 
 * @author anelson, bstice 
 * Creates a wrapper class for the TestRunCase
 */
public class TestCaseRun {
	
	private int caseID;
	private int runID;
	private int buildID;
	private int environmentID;
	private Integer caseRunID; 
	
	//checks which constructor is used
	private boolean canUpdate;
	
	//stores if the variable needs to updated
	private boolean isSetNotes = false; 
	private boolean isSetStatus = false; 
	private boolean isSetAssigneeID = false; 
	private boolean isSetBuildID = false;
	private boolean isSetEnvironmentID = false;  
		
	//stores the updated value until it's pushed to tesopia with an update
	private String notes;
	private int caseStatus;
	private int assigneeID;
	private int build_ID; 
	private int environment_ID;
	
	private HttpState httpState;
	private Session session;
	
	/**
	 * Use this constructor if you just want to use gets
	 * @param userName your bugzilla username
	 * @param password your bugzilla password
	 * @param caseRunID ID generated by bugzilla - can be null
	 * @param url URL - the url of the testopia server that you want to connect to
	 */
	public TestCaseRun(Session session, int caseRunID)
	{
		this.session   = session; 
		this.caseRunID = caseRunID; 
		this.canUpdate = false;
	}
	
	/**
	 * Use this constructor if you want to do sets and gets
	 * @param userName your bugzilla username
	 * @param password your bugzilla password 
	 * @param caseID ID used to get the case
	 * @param runID test run number
	 * @param buildID ID generated by bugzilla
	 * @param environmentID ID generated by bugzilla
	 * @param caseRunID ID generated by bugzilla - can be null
	 * @param url URL - the url of the testopia server that you want to connect to
	 */
	public TestCaseRun(Session session, int caseID,
			int runID, int buildID, int environmentID, Integer caseRunID) 
	{
		this.session = session;
		this.caseID = caseID;
		this.runID = runID; 
		this.buildID = buildID; 
		this.environmentID = environmentID;
		this.caseRunID = caseRunID;
		this.canUpdate = true;
	}
	
	/**
	 * used to create a testRunCase
	 * @param assigneeID
	 * @param caseRunStatusID
	 * @param caseTextVersion
	 * @return caseRunID
	 * @throws Exception
	 */
	public int makeTestCaseRun(int assigneeID, int caseTextVersion) throws Exception
	{
		if (canUpdate == false) 
		{
			throw new Exception(
					"You can't update if you use the 3 parameter constructor, you must use the constuctor with 7 parameters");
		}
		
	    //set the values for the test case
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("assignee", assigneeID);
		map.put("case_id", caseID);
		map.put("case_text_version", caseTextVersion);
		map.put("environment_id", environmentID);
		map.put("run_id", runID);
		map.put("build_id", buildID);
		
		//System.out.println("assignee: "+assigneeID+"\ncase_id: "+caseID+"\ncase_text_version: "+caseTextVersion+"\nenvironment_id: "+environmentID+"\nrun_id: "+runID+"\nbuild_id: "+buildID);
				
		try 
		{

			XmlRpcClient client = session.getClient();

			ArrayList<Object> params = new ArrayList<Object>();
			
			//set up params, to identify the test case
			params.add(map);
			

			//update the test case
			int result = (Integer)client.execute("TestCaseRun.create",
					params);
			
			caseRunID = result; 
			//System.out.println(result);	
			return result;
			
		}			
		
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("ERROR: Unable to create new TestCaseRun");
			return 0;
		}
	}
	
	/**
	 * Updates are not called when the .set is used. You must call update after all your sets
	 * to push the changes over to testopia.
	 * @throws Exception will throw an exception if you used the 3 param constuctor. 
	 */
	public void update() throws Exception
	{
		if (canUpdate == false) 
		{
			throw new Exception(
					"You can't update if you use the 3 parameter constructor, you must use the constuctor with 7 parameters");
		}
		
		//hashmap to store attributes to be updated
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		//add attributes that need to be updated to the hashmap 
		if(isSetNotes)
			map.put("notes", notes);
		if(isSetStatus)
			map.put("case_run_status_id", caseStatus); 
		if(isSetAssigneeID)
			map.put("assignee", assigneeID);
		if(isSetBuildID)
			map.put("build_id", build_ID);
		if(isSetEnvironmentID)
			map.put("environment_id", environment_ID);
		
		try 
		{

			XmlRpcClient client = session.getClient();

			ArrayList<Object> params = new ArrayList<Object>();
			
			//set up params, to identify the test case
			params.add(runID);
			params.add(caseID);
			params.add(buildID);
			params.add(environmentID);
			params.add(map);

			//update the testRunCase
			HashMap result = (HashMap) client.execute("TestCaseRun.update",
					params);
			
			//System.out.println(result);
			
			//make sure multiple updates aren't called, for one set
			isSetAssigneeID = false;
			isSetBuildID = false;
			isSetEnvironmentID = false; 
			isSetNotes = false; 
			isSetStatus = false; 
			
		}			
		
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return a hashMap of all the values found. Returns null if there is an error
	 * and the TestCaseRun cannot be returned
	 */
	public HashMap<String, Object> getAttributes()
	{
		try 
		{

			XmlRpcClient client = session.getClient();
			ArrayList<Object> params = new ArrayList<Object>();
			
			//set up params, to identify the test case
			params.add(caseRunID);
			
			//update the testRunCase
			HashMap result = (HashMap) client.execute("TestCaseRun.get",
					params);
			
			//System.out.println(result);
			
			return result;
		
			
		}			
		
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This is used to append a note
	 * @param notes string - the note you want entered into the testCaseRun
	 */
	public void setNotes(String notes)
	{
		isSetNotes = true;		
		this.notes = notes;
	}
	
	/**
	 * This is used to change the testCaseRun status (2 for pass, 3 for fail)
	 * @param status int - the status you want to change the testCaseRun to
	 */
	public void setStatus(int status)
	{
		isSetStatus = true;
		this.caseStatus = status;
	}
	
	/**
	 * Changes the buildID of the testCaseRun
	 * @param buildID int - the new buildID
	 */
	public void setBuildID(int buildID)
	{
		isSetBuildID = true; 
		this.build_ID = buildID; 
	}
	
	/**
	 * Changes the environmentID of the testCaseRun
	 * @param environmentID int - the number that the environment ID will be changed to
	 */
	public void setEnvironmentID(int environmentID)
	{
		isSetEnvironmentID = true;
		this.environment_ID = environmentID;
	}
	
	
	/**
	 * Changes the assigneeID of the testCaseRun
	 * @param assigneeID
	 */
	public void setAssigneeID(int assigneeID)
	{
		isSetAssigneeID = true; 
		this.assigneeID = assigneeID; 
	}
	
}
