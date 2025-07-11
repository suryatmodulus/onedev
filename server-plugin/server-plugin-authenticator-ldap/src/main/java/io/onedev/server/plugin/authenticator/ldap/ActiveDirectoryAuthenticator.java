package io.onedev.server.plugin.authenticator.ldap;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import io.onedev.server.annotation.Editable;
import io.onedev.server.annotation.ShowCondition;

import java.util.List;

@Editable(name="Active Directory", order=100)
public class ActiveDirectoryAuthenticator extends LdapAuthenticator {

	private static final long serialVersionUID = 1L;

	private String groupSearchBase;
	
    @Editable(order=100, name="LDAP URL", description = "" +
			"Specifies LDAP URL of the Active Directory server, for example: <i>ldap://ad-server</i>, or <i>ldaps://ad-server</i>. " +
			"In case your ldap server is using a self-signed certificate for ldaps connection, you will need to " +
			"<a href='https://docs.onedev.io/administration-guide/trust-self-signed-certificates' target='_blank'>configure OneDev to trust the certificate</a>")
    @NotEmpty
	@Override
	public String getLdapUrl() {
		return super.getLdapUrl();
	}

	@Override
	public void setLdapUrl(String ldapUrl) {
		super.setLdapUrl(ldapUrl);
	}

	@Editable(order=300, description=""
			+ "Specify manager DN to authenticate OneDev itself to Active Directory. The manager DN should be specified "
			+ "in form of <i>&lt;account name&gt;@&lt;domain&gt;</i>, for instance: <i>manager@example.com</i>")
	@NotEmpty
	@ShowCondition("isAuthenticationRequiredEnabled")
	@Override
	public String getManagerDN() {
		return super.getManagerDN();
	}

	@Override
	public void setManagerDN(String managerDN) {
		super.setManagerDN(managerDN);
	}

	@Editable(order=500, placeholder = "Input user search bases. Hit ENTER to add", description=
		"Specify base nodes for user search. For example: <i>cn=Users, dc=example, dc=com</i>")
	@Size(min=1, message = "At least one user search base should be specified")
	@Override
	public List<String> getUserSearchBases() {
		return super.getUserSearchBases();
	}

	@Override
	public void setUserSearchBases(List<String> userSearchBases) {
		super.setUserSearchBases(userSearchBases);
	}

	@Override
	public String getUserSearchFilter() {
		return "(&(sAMAccountName={0})(objectclass=user))";
	}
    
	@Override
	public void setUserSearchFilter(String userSearchFilter) {
		super.setUserSearchFilter(userSearchFilter);
	}

	@Editable(order=1000, placeholder="Do not retrieve groups", description=""
			+ "Optionally specify group search base if you want to retrieve group membership information "
			+ "of the user. For example: <i>cn=Users, dc=example, dc=com</i>. To give appropriate "
			+ "permissions to a Active Directory group, a OneDev group with same name should be defined. "
			+ "Leave empty to manage group memberships at OneDev side")
	public String getGroupSearchBase() {
		return groupSearchBase;
	}

	public void setGroupSearchBase(String groupSearchBase) {
		this.groupSearchBase = groupSearchBase;
	}

	@Override
	public GroupRetrieval getGroupRetrieval() {
		if (getGroupSearchBase() != null) {
			SearchGroupsUsingFilter groupRetrieval = new SearchGroupsUsingFilter();
			groupRetrieval.setGroupSearchBase(getGroupSearchBase());
			groupRetrieval.setGroupSearchFilter("(&(member:1.2.840.113556.1.4.1941:={0})(objectclass=group))");
			return groupRetrieval;
		} else {
			return new DoNotRetrieveGroups();
		}
	}

}
