# Morpho_Omni

### Plugin description

* Detect bacteria on phase contrast channel with Omnipose
* Measure bacterium area, length (as Feret diameter), width (as min Feret diameter), circularity, roundness and aspect ratio in calibrated units and save them in an excel file
* Save image with segmentation masks overlayed for each frame

### Dependencies

* **3DImageSuite** Fiji plugin
* **Omnipose** conda environment + *bact_phase_omnitorch_0* model

This plugin structure and code (namely the Cellpose wrapper) were inspired from a plugin of the Orion-Cirb github repository.
