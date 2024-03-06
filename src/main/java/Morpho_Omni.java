import Morpho_Omni_Tools.Tools;
import ij.*;
import ij.plugin.Duplicator;
import ij.plugin.PlugIn;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.plugins.BF;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.in.ImporterOptions;
import mcib3d.geom2.Objects3DIntPopulation;
import org.apache.commons.io.FilenameUtils;
import org.scijava.util.ArrayUtils;


/**
 * Detect bacteria in phase contrast with Omnipose and compute some morphological descriptors
 */
public class Morpho_Omni implements PlugIn {
    
    Tools tools = new Tools();
    private String imageDir = "";
    public String outDirResults = "";
    public BufferedWriter results;
   
    
    public void run(String arg) {
        try {
            if (!tools.checkInstalledModules()) {
                return;
            } 
            
            imageDir = IJ.getDirectory("Choose directory containing image files...");
            if (imageDir == null) {
                return;
            }  
            
            // Find images with extension
            String file_ext = tools.findImageType(new File(imageDir));
            ArrayList<String> imageFiles = tools.findImages(imageDir, file_ext);
            if (imageFiles.isEmpty()) {
                IJ.showMessage("Error", "No images found with " + file_ext + " extension");
                return;
            }
            
            // Create output folder
            outDirResults = imageDir + File.separator + "Results" + File.separator;
            File outDir = new File(outDirResults);
            if (!Files.exists(Paths.get(outDirResults))) {
                outDir.mkdir();
            }
            // Write header in results file
            String header = "Image name\tFrame number\tBact ID\t Bacterium area\tBacterium feret\t"
                    + "Bacterium feret min\tBacterium cicularity\t"
                    + "Bacterium aspect ratio\t"+ "Bacterium roundness\n";
            FileWriter fwResults = new FileWriter(outDirResults + "results.xls", false);
            results = new BufferedWriter(fwResults);
            results.write(header);
            results.flush();
            
            // Create OME-XML metadata store of the latest schema version
            ServiceFactory factory;
            factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            IMetadata meta = service.createOMEXMLMetadata();
            ImageProcessorReader reader = new ImageProcessorReader();
            reader.setMetadataStore(meta);
            reader.setId(imageFiles.get(0));
            
            // Find image calibration
            tools.findImageCalib(meta);
            
            // Find channels name
            String[] channels = tools.findChannels(imageFiles.get(0), meta, reader);
            
            // Dialog box
            String[] chs = tools.dialog(channels);
            if (chs == null) {
                IJ.showMessage("Error", "Plugin canceled");
                return;
            } 

            for (String f : imageFiles) {
                reader.setId(f);
                String rootName = FilenameUtils.getBaseName(f);
                tools.print("--- ANALYZING IMAGE " + rootName + " ------");
                
                ImporterOptions options = new ImporterOptions();
                options.setId(f);
                options.setQuiet(true);
                options.setColorMode(ImporterOptions.COLOR_MODE_GRAYSCALE);
                options.setSplitChannels(true);
                
                // Open phase contrast channel
                int indexCh = ArrayUtils.indexOf(channels, chs[0]);
                ImagePlus imgPhase = BF.openImagePlus(options)[indexCh];
                
                for(int t=1; t < imgPhase.getNFrames() + 1; t++) {
                    
                    // Open frame t for phase contrast channel
                    ImagePlus tPhase = new Duplicator().run​(imgPhase, 1, 1, 1, 1, t, t);
                    
                    // Detect bacteria with Omnipose
                    Objects3DIntPopulation tbactPop = tools.omniposeDetection(tPhase);
                    System.out.println(tbactPop.getNbObjects() + " bacteria found");
                    
                    // Do measurements and save results
                    tools.saveResults(tbactPop, tPhase, rootName, results, t);
                    
                    // Draw results
                    tools.drawResults(tPhase, tbactPop, outDirResults+rootName, outDirResults);
                
                }
 
                tools.flush_close(imgPhase);
            }

            tools.print("--- Done! ---");
            
        }   catch (IOException | FormatException | DependencyException | ServiceException ex) {
            Logger.getLogger(Morpho_Omni.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
}    
