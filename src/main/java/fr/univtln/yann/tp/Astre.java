package fr.univtln.yann.tp;


import com.jme3.asset.AssetManager;
import com.jme3.input.ChaseCamera;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.*;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

public class Astre {

    // Variables :
    private float angle; 
    private float distanceDuSoleil;
    private float distanceMinDuSoleil;
    private float distanceMaxDuSoleil;
    private float G;
    private float masseSoleil;
    private float vitesseTranslation;

    // Paramètres :
    private float rayon;
    private String nom;
    private float vitesseRotation ;
    private float angleRotation ;
    private float x;
    private float y;
    private float z;

    //  Pivots
    private Node pivot;
    private Node pose;
    private Node Mere;
    private Node camNode;


    // Objet 3D :
    private Sphere sphere;
    private Geometry geom;
    private Material mat;
    private Texture texture;
    private AssetManager assetManager;

    private Texture textureNormal;

    private CameraNode cameraNode;

    public Astre( String nom, float rayon, float vitesseRotation, float angleRotation,
    String nomTexture, String nomTextureNormal, AssetManager assetManager,
    float distanceDuSoleil, float distanceMinDuSoleil, float distanceMaxDuSoleil, float G, float masseSoleil, Node Mere) {
        this.angle = 0f;
        this.distanceDuSoleil = distanceDuSoleil;
        this.distanceMinDuSoleil = distanceMinDuSoleil;
        this.distanceMaxDuSoleil = distanceMaxDuSoleil;
        this.G = G;
        this.masseSoleil = masseSoleil;
        this.vitesseTranslation = -FastMath.sqrt(G * masseSoleil / distanceDuSoleil);

        this.nom = nom;
        this.rayon = rayon;
        this.assetManager = assetManager;
        this.vitesseRotation = vitesseRotation;
        this.angleRotation = angleRotation;


        this.sphere = new Sphere(32,32,this.rayon);
        this.sphere.setTextureMode(Sphere.TextureMode.Projected);
        //TangentBinormalGenerator.generate(this.sphere, true);
        this.geom = new Geometry(this.nom+"_Geometry",this.sphere);
        // this.mat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        this.mat = new Material(this.assetManager, "Common/MatDefs/Light/Lighting.j3md");
        this.texture = this.assetManager.loadTexture(nomTexture);
        this.mat.setTexture("DiffuseMap", this.texture);

        

        if (nomTextureNormal != null)
        {
            TangentBinormalGenerator.generate(this.sphere, true);
            this.textureNormal = this.assetManager.loadTexture(nomTextureNormal);
            this.mat.setTexture("NormalMap", this.textureNormal);
        }
        
        this.mat.setBoolean("UseMaterialColors",true);
        this.mat.setColor("Diffuse",ColorRGBA.White);
        this.mat.setColor("Specular",ColorRGBA.White);
        this.mat.setFloat("Shininess", 128f);  // [0,128]

        this.geom.setMaterial(this.mat);

        this.pivot = new Node(this.nom+"_pivot");
        

        this.pose = new Node(this.nom+"_pose");
        this.pose.attachChild(this.pivot);
        this.pivot.attachChild(this.geom);

        this.geom.getMesh().scaleTextureCoordinates(new Vector2f(1, 1));
        this.geom.rotate(-FastMath.HALF_PI, 0, 0); //Rotation pour la texture
        // this.pivot.rotate(-this.angleRotation,0,0);
        this.pivot.rotate(-this.angleRotation,0,0);
        this.Mere = Mere;
        // this.Mere.attachChild(this.pivot);
        // this.pivot.setLocalTranslation(Mere.getLocalTranslation());
        // this.pivot.setLocalTranslation(new Vector3f(this.distanceDuSoleil,0,0));
        this.Mere.attachChild(this.pose);
        this.pose.setLocalTranslation(Mere.getLocalTranslation());
        this.pose.setLocalTranslation(new Vector3f(this.distanceDuSoleil,0,0));

        //Création de la caméra
        this.camNode = new Node("CamNode"+this.nom);
        //this.pivot.attachChild(this.camNode);
        this.pose.attachChild(this.camNode);
        //this.camNode.setLocalTranslation(pose.getLocalTranslation());
        //this.camNode.setLocalTranslation(new Vector3f(this.distanceDuSoleil+this.rayon*2,0,0));
        


    }

    public void majVitesse()
    {
        this.distanceDuSoleil = calculerDistanceDuSoleil();
        this.vitesseTranslation = -FastMath.sqrt(G * masseSoleil / distanceDuSoleil);
    }

    public float calculerDistanceDuSoleil()
    {
        // return (float) Math.sqrt(Math.pow(this.pivot.getLocalTranslation().x,2) + Math.pow(this.pivot.getLocalTranslation().z,2));
        return (float) Math.sqrt(Math.pow(this.pose.getLocalTranslation().x,2) + Math.pow(this.pose.getLocalTranslation().z,2));
    }

    public void rotation(float tpf)
    {
        this.pivot.rotate(0, tpf * vitesseRotation, 0);
        //this.camNode.rotate(0, tpf * vitesseRotation, 0);
    }

    public void translation(float tpf)
    {
        majVitesse();
        angle += vitesseTranslation * tpf;
        x = distanceMinDuSoleil * (float) Math.cos(angle);
        z = distanceMaxDuSoleil * (float) Math.sin(angle);
        pose.setLocalTranslation(new Vector3f(x,0,z));
    }

    public void mouvement(float tpf)
    {
        translation(tpf);
        rotation(tpf);
    }

    public float getAngle() {
        return angle;
    }

    public float getDistanceDuSoleil() {
        return distanceDuSoleil;
    }

    public float getDistanceMinDuSoleil() {
        return distanceMinDuSoleil;
    }

    public float getDistanceMaxDuSoleil() {
        return distanceMaxDuSoleil;
    }   

    public float getG() {
        return G;
    }

    public float getMasseSoleil() {
        return masseSoleil;
    }

    public float getVitesseTranslation() {
        return vitesseTranslation;
    }

    public float getVitesseRotation() {
        return vitesseRotation;
    }

    public float getRayon() {
        return rayon;
    }

    public String getNom() {
        return nom;
    }

    public Sphere getSphere() {
        return sphere;
    }

    public Node getPivot() {
        return pivot;
    }

    public Geometry getGeom() {
        return geom;
    }

    public Material getMat() {
        return mat;
    }

    public Texture getTexture() {
        return texture;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public Node getMere() {
        return Mere;
    }

    public CameraNode getCameraNode() {
        return cameraNode;
    }

    public Node getCamNode() {
        return camNode;
    }

    public Texture getTextureNormal() {
        return textureNormal;
    }

    public Node getPose() {
        return pose;
    }
    

    public void setCamNode(CameraNode camNode) {
        this.camNode = camNode;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setDistanceDuSoleil(float distanceDuSoleil) {
        this.distanceDuSoleil = distanceDuSoleil;
        majVitesse();
    }

    public void setDistanceMinDuSoleil(float distanceMinDuSoleil) {
        this.distanceMinDuSoleil = distanceMinDuSoleil;
    }

    public void setDistanceMaxDuSoleil(float distanceMaxDuSoleil) {
        this.distanceMaxDuSoleil = distanceMaxDuSoleil;
    }

    public void setG(float g) {
        G = g;
        majVitesse();
    }

    public void setMasseSoleil(float masseSoleil) {
        this.masseSoleil = masseSoleil;
        majVitesse();
    }

    public void setVitesseTranslation(float vitesseTranslation) {
        this.vitesseTranslation = vitesseTranslation;
    }

    public void setRayon(float rayon) {
        this.rayon = rayon;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSphere(Sphere sphere) {
        this.sphere = sphere;
    }

    public void setPivot(Node pivot) {
        this.pivot = pivot;
    }

    public void setGeom(Geometry astre) {
        this.geom = astre;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void setMere(Node mere) {
        Mere = mere;
    }

    public void setCameraNode(CameraNode cameraNode) {
        this.cameraNode = cameraNode;
    }

    public void setVitesseRotation(float vitesseRotation) {
        this.vitesseRotation = vitesseRotation;
    }

    public void setTextureNormal(Texture textureNormal) {
        this.textureNormal = textureNormal;
    }

    public void setPose(Node pose) {
        this.pose = pose;
    }
    
}


