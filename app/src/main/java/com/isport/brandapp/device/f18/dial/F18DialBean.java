package com.isport.brandapp.device.f18.dial;

/**
 * Created by Admin
 * Date 2022/2/9
 */

public class F18DialBean {


    private Integer id;
    private String dialNum;
    private Integer type;
    private String lcd;
    private String toolVersion;
    private String binUrl;
    private String binVersion;
    private String customer;
    private String name;
    private Integer downloadCount;
    private String previewImgUrl;
    private Integer hasComponent;
    private String componentsRaw;
    private String relatedProject;
    private String components;
    private Integer binSize;
    private Integer status;
    private Object creatorName;
    private String createTime;
    private Object typeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDialNum() {
        return dialNum;
    }

    public void setDialNum(String dialNum) {
        this.dialNum = dialNum;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLcd() {
        return lcd;
    }

    public void setLcd(String lcd) {
        this.lcd = lcd;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    public String getBinUrl() {
        return binUrl;
    }

    public void setBinUrl(String binUrl) {
        this.binUrl = binUrl;
    }

    public String getBinVersion() {
        return binVersion;
    }

    public void setBinVersion(String binVersion) {
        this.binVersion = binVersion;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }

    public Integer getHasComponent() {
        return hasComponent;
    }

    public void setHasComponent(Integer hasComponent) {
        this.hasComponent = hasComponent;
    }

    public String getComponentsRaw() {
        return componentsRaw;
    }

    public void setComponentsRaw(String componentsRaw) {
        this.componentsRaw = componentsRaw;
    }

    public String getRelatedProject() {
        return relatedProject;
    }

    public void setRelatedProject(String relatedProject) {
        this.relatedProject = relatedProject;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public Integer getBinSize() {
        return binSize;
    }

    public void setBinSize(Integer binSize) {
        this.binSize = binSize;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(Object creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Object getTypeName() {
        return typeName;
    }

    public void setTypeName(Object typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "F18DialBean{" +
                "id=" + id +
                ", dialNum='" + dialNum + '\'' +
                ", type=" + type +
                ", lcd='" + lcd + '\'' +
                ", toolVersion='" + toolVersion + '\'' +
                ", binUrl='" + binUrl + '\'' +
                ", binVersion='" + binVersion + '\'' +
                ", customer='" + customer + '\'' +
                ", name='" + name + '\'' +
                ", downloadCount=" + downloadCount +
                ", previewImgUrl='" + previewImgUrl + '\'' +
                ", hasComponent=" + hasComponent +
                ", componentsRaw='" + componentsRaw + '\'' +
                ", relatedProject='" + relatedProject + '\'' +
                ", components='" + components + '\'' +
                ", binSize=" + binSize +
                ", status=" + status +
                ", creatorName=" + creatorName +
                ", createTime='" + createTime + '\'' +
                ", typeName=" + typeName +
                '}';
    }
}
