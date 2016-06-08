package com.cintriq.agenda.web.controllers;


import com.cintriq.agenda.core.AssetTypeService;
import com.cintriq.agenda.core.TagService;
import com.cintriq.agenda.core.entity.AssetType;
import com.cintriq.agenda.core.entity.Tag;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pasha on 6/7/2016.
 */
@ManagedBean(name = "TagController")
@ViewScoped
public class TagController {


    @Inject
    private TagService tagService;

    @Inject
    private AssetTypeService assetTypeService;


    public void newAssetTypeTag(AssetType assetType, String tagName){
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setAssetType(assetType);
        try {
            tagService.insert(tag);
            System.out.println("Tag persisted successfully");
        } catch (Exception e) {
            System.out.println("Tag not persisted. An error occurred");
            e.printStackTrace();
        }
        //get latest from assetType
        assetType = assetTypeService.get(assetType.getId());

        //print tags for test
        for(Tag TagPrint : assetType.getTags()){
            System.out.println(TagPrint);
        }
    }

//bad codez
/*
    public  void deleteTagFromAssetType(AssetType assetType, Tag tag){
        try {
            //not sure if this will delete random assetType?
            assetTypeService.get(assetType.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


    //remove tag by string param
    public void deleteTagfromAssetType(AssetType assetType, String tagName){
        Tag tag = tagService.findByName(assetType, tagName);
        try {
            tagService.delete(tag.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTag(Tag tag){

       //tag = tagService.get(tag.getId());
        try {
            tagService.delete(tag.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
