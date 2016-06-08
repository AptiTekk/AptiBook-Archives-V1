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
         if(assetType != null && tagName!= null) {
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
         }else {
             System.out.print("params are null");
         }
        //get latest from assetType
        assetType = assetTypeService.get(assetType.getId());

        //print tags for test
        for(Tag tagPrint : assetType.getTags()){
            System.out.println(tagPrint.getName());

        }
    }


    //remove tag by string param
    public void deleteTagfromAssetType(AssetType assetType, String tagName){
        if(assetType != null && tagName!= null) {
            Tag tag = tagService.findByName(assetType, tagName);
            try {
                if(tag != null) {
                    tagService.delete(tag.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Params are null");
        }
    }

    public void deleteTag(Tag tag){

       //tag = tagService.get(tag.getId());
        try {
            if(tag != null) {
                tagService.delete(tag.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
