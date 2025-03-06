package fr.univtln.yann.tp;


import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
// import com.jme3.math.Matrix3f;
// import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.control.CameraControl;
import com.jme3.system.AppSettings;
import com.jme3.util.TangentBinormalGenerator;
//import com.jme3.util.TangentBinormalGenerator;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

// https://www.solarsystemscope.com/textures/

// import com.jme3.texture.Texture;
/**
 * Hello world!
 */
public class App extends SimpleApplication {

    // Vector3f v = new Vector3f(15,0.0f,0);
    Node pivot = new Node("pivot");
    Node pivot2 = new Node("pivot2");
    // Quaternion angle;
    int cpt = 0;

    // Variables :
    float angle = 0f;
    float distancesoleil = 10f;
    float G = 1f;
    float masseSoleil = 100f;

    Astre terre;
    Astre lune;

    int pause = 0;
    float vitesse = 0f;

    //CameraNode camNode;
    //ChaseCamera chaseCam;

    BitmapText helloText;
    float rot_terre = 0;
    float rot_terre_prev = 0;

    public static void main(String[] args) {
        App app = new App();
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        app.setSettings(settings);
        app.start();
    }

  
    public void simpleInitApp()
    {

        // Initialisation de la caméra
        //camNode = new CameraNode("CamNode", cam);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));

        flyCam.setMoveSpeed(10);
        flyCam.setEnabled(false);

        //Création Du Soleil
        assetManager.registerLocator("assets", com.jme3.asset.plugins.FileLocator.class);
        
        Sphere sphere = new Sphere(32,32,5f);
        //TangentBinormalGenerator.generate(sphere);
        Geometry sun = new Geometry("Sun",sphere);
        sun.rotate(-FastMath.HALF_PI, 0, 0);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setTexture("ColorMap", assetManager.loadTexture("Textures/8k_sun.jpg"));
        //mat1.setColor("Diffuse", ColorRGBA.White);
        //mat1.setColor("Specular", ColorRGBA.White);
        //mat1.setColor("GlowColor", ColorRGBA.White.mult(3f));
        sun.setMaterial(mat1);
        pivot = new Node("pivot");
        pivot.attachChild(sun);
        rootNode.attachChild(pivot);

        //Création de la lumière
        PointLight sunLight = new PointLight();
        sunLight.setColor(ColorRGBA.White);
        //sunLight.setRadius(10000f);
        Node pivot_light = new Node("pivot_light");
        rootNode.addLight(sunLight);
        //rootNode.attachChild(pivot_light);
        
        /// sunLight.setPosition(new Vector3f(0,-5,0));
        rootNode.attachChild(pivot_light);
        



        //Création des planètes

        //Création de la Terre
        terre = new Astre("Terre", 2f, 24f, FastMath.DEG_TO_RAD * 23.5f,
         "Textures/earth.jpeg", "Textures/earth_norm2.png", assetManager,
           10f,9.8f,10.2f,
            G, 100f, pivot);
        rootNode.attachChild(terre.getPose());

        lune = new Astre("Lune", 1f, 24f, FastMath.DEG_TO_RAD * 0,
         "Textures/8k_moon.jpg", "Textures/moon_norm.png", assetManager,
           5,5f,5f, 
           G, 1f, terre.getPose());
        rootNode.attachChild(terre.getPose());
        //terre.getPose().setLocalTranslation(terre.getDistanceDuSoleil(),0,0);
        //terre.getPivot().attachChild(camNode);
        //camNode.setLocalTranslation(terre.getPivot().getLocalTranslation().add(new Vector3f(0,0,2)));
        //camNode.lookAt(terre.getPivot().getLocalTranslation(), Vector3f.UNIT_Y);
        //camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);

        terre.setCameraNode(new CameraNode("cam"+terre.getNom(), cam));
        terre.getCamNode().attachChild(terre.getCameraNode());
        terre.getCameraNode().setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        terre.getCameraNode().setLocalTranslation(terre.getCamNode().getLocalTranslation().add(terre.getRayon() * 10,0,0));
        terre.getCameraNode().lookAt(terre.getCamNode().getLocalTranslation(), Vector3f.UNIT_Y);
        System.out.println(terre.getCamNode().getWorldTranslation());
        System.out.println(terre.getPose().getWorldTranslation());

        

        

        rootNode.move(new Vector3f(0,-0,0));
        initKeys();

        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        helloText = new BitmapText(guiFont);
        helloText.setSize(guiFont.getCharSet().getRenderedSize());
        helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        guiNode.attachChild(helloText);
        System.out.println("camera terre"+"\n"+terre.getCameraNode().getLocalTranslation()+"\n"+terre.getPose().getLocalTranslation());
    }   

    public void simpleUpdate(float tpf)
    {
        rot_terre_prev = rot_terre;
        terre.mouvement(tpf * vitesse * pause);
        lune.mouvement(tpf * vitesse * pause);
        //terre.rotation(tpf * vitesse * pause);
        rot_terre = terre.getPivot().getLocalRotation().getY();
        if( rot_terre >= 0 && rot_terre_prev < 0)
        {
            cpt++;
        }
        helloText.setText("Jour terre : " + cpt);
        //terre.camNode.rotate(0, tpf*vitesse, 0);
    }

    private void initKeys()
    {
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Accelerer", new KeyTrigger(KeyInput.KEY_ADD));
        inputManager.addMapping("Ralentir", new KeyTrigger(KeyInput.KEY_SUBTRACT));
        inputManager.addMapping("Droite", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Gauche", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Haut", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Bas", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Zoomer", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Dezoumer", new KeyTrigger(KeyInput.KEY_S));


        inputManager.addListener(actionListener, "Pause");
        inputManager.addListener(analogListener, "Accelerer", "Ralentir", "Droite", "Gauche", "Haut", "Bas", "Zoomer", "Dezoumer");

    }

    final ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("Pause") && !keyPressed)
            {
                if(pause == 1)
                {
                    pause = 0;
                }
                else
                {
                    pause = 1;
                }
            }
        }
    };

    final AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if(name.equals("Accelerer"))
            {
                vitesse += 0.01f;
            }
            if(name.equals("Ralentir"))
            {
                vitesse -= 0.01f;
            }
            if(name.equals("Droite"))
            {
                terre.getCamNode().rotate(0, 0.1f, 0);
                System.out.println(terre.getCamNode().getLocalRotation());
                System.out.println(terre.getCamNode().getLocalTranslation());
                System.out.println(terre.getPose().getLocalTranslation());
            }
            if(name.equals("Gauche"))
            {
                terre.getCamNode().rotate(0, -0.1f, 0);
                System.out.println(terre.getCamNode().getLocalRotation());
                System.out.println(terre.getCamNode().getLocalTranslation());
                System.out.println(terre.getPose().getLocalTranslation());
            }

            if(name.equals("Haut"))
            {
                terre.getCamNode().rotate(0, 0, 0.1f);
                System.out.println(terre.getCamNode().getLocalRotation());
                System.out.println(terre.getCamNode().getLocalTranslation());
                System.out.println(terre.getPose().getLocalTranslation());
            }
            if(name.equals("Bas"))
            {
                terre.getCamNode().rotate(0, 0, -0.1f);
                System.out.println(terre.getCamNode().getLocalRotation());
                System.out.println(terre.getCamNode().getLocalTranslation());
                System.out.println(terre.getPose().getLocalTranslation());
            }
            if(name.equals("Zoomer"))
            {
                terre.getCameraNode().move(-0.1f,0,0);
                System.out.println(terre.getCamNode().getLocalRotation());
                System.out.println(terre.getCamNode().getLocalTranslation());
                System.out.println(terre.getPose().getLocalTranslation());
            }
            if(name.equals("Dezoumer"))
            {
                terre.getCameraNode().move(0.1f,0,0);
                System.out.println(terre.getCamNode().getLocalRotation());
                System.out.println(terre.getCamNode().getLocalTranslation());
                System.out.println(terre.getPose().getLocalTranslation());
            }
        }
    };
}

