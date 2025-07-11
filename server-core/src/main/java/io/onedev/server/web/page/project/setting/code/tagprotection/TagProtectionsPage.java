package io.onedev.server.web.page.project.setting.code.tagprotection;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.ProjectManager;
import io.onedev.server.model.support.code.TagProtection;
import io.onedev.server.util.CollectionUtils;
import io.onedev.server.web.behavior.sortable.SortBehavior;
import io.onedev.server.web.behavior.sortable.SortPosition;
import io.onedev.server.web.page.project.setting.ProjectSettingPage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static io.onedev.server.web.translation.Translation._T;

import java.util.List;

public class TagProtectionsPage extends ProjectSettingPage {

	private WebMarkupContainer container;
	
	public TagProtectionsPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		container = new WebMarkupContainer("tagProtectionSetting");
		container.setOutputMarkupId(true);
		add(container);
		container.add(new ListView<>("protections", new AbstractReadOnlyModel<List<TagProtection>>() {

			@Override
			public List<TagProtection> getObject() {
				return getProject().getTagProtections();
			}
		}) {

			@Override
			protected void populateItem(final ListItem<TagProtection> item) {
				item.add(new TagProtectionPanel("protection", item.getModelObject()) {

					@Override
					protected void onDelete(AjaxRequestTarget target) {
						getProject().getTagProtections().remove(item.getIndex());
						OneDev.getInstance(ProjectManager.class).update(getProject());
						target.add(container);
					}

					@Override
					protected void onSave(AjaxRequestTarget target, TagProtection protection) {
						getProject().getTagProtections().set(item.getIndex(), protection);
						OneDev.getInstance(ProjectManager.class).update(getProject());
						target.add(container);
					}

					@Override
					protected void onCancel(AjaxRequestTarget target) {
						target.add(container);
					}
					
				});
			}

		});
		
		container.add(new SortBehavior() {
			
			@Override
			protected void onSort(AjaxRequestTarget target, SortPosition from, SortPosition to) {
				List<TagProtection> protections = getProject().getTagProtections();
				CollectionUtils.move(protections, from.getItemIndex(), to.getItemIndex());
				OneDev.getInstance(ProjectManager.class).update(getProject());
				
				target.add(container);
			}
			
		}.items("li.protection").handle(".card-header"));
		
		container.add(newAddNewFrag());
	}

	private Component newAddNewFrag() {
		Fragment fragment = new Fragment("newProtection", "addNewLinkFrag", this); 
		fragment.add(new AjaxLink<Void>("link") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				Fragment fragment = new Fragment("newProtection", "editNewFrag", getPage());
				fragment.setOutputMarkupId(true);
				fragment.add(new TagProtectionEditPanel("editor", new TagProtection()) {

					@Override
					protected void onSave(AjaxRequestTarget target, TagProtection protection) {
						getProject().getTagProtections().add(protection);
						OneDev.getInstance(ProjectManager.class).update(getProject());
						container.replace(newAddNewFrag());
						target.add(container);
					}

					@Override
					protected void onCancel(AjaxRequestTarget target) {
						Component newAddNewFrag = newAddNewFrag();
						container.replace(newAddNewFrag);
						target.add(newAddNewFrag);
					}
					
				});
				container.replace(fragment);
				target.add(fragment);
			}
			
		});
		fragment.setOutputMarkupId(true);
		return fragment;
	}

	@Override
	protected Component newProjectTitle(String componentId) {
		return new Label(componentId, _T("Tag Protection"));
	}
	
}
