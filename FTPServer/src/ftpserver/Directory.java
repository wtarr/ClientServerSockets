package ftpserver;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 12/10/13
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class Directory {
    private String owner;
    private ArrayList<String> dirListing;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<String> getDirListing() {
        return dirListing;
    }

    public void setDirListing(ArrayList<String> dirListing) {
        this.dirListing = dirListing;
    }

    public Directory() {
           dirListing = new ArrayList<String>();           
    }
}
