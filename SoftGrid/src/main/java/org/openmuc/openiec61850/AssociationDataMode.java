package org.openmuc.openiec61850;

/**
 * Created by prageethmahendra on 29/1/2016.
 * This e num defines how the data source should be integrated to the server
 */
public enum AssociationDataMode {
    // self managed data association will use the open_muc data manager to read and write data
    SELF_MANAGED_DATA,
    // integrated data asscociation will integration the ied server with another third party data source
    INTEGRATED_DATA
}
