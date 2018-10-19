package  com.lsenseney.orpheus.plugin;
import com.lsenseney.orpheus.Environment;
/**
 *
 * @author Luke Senseney
 * @version 1.0 Oct 03, 18
 **/
public interface Dependency extends Cloneable{
    public String getName();
    public void configureEnvironement(Environment e) throws ConfigurationFailedException;

    public void markSuccess();
    public void markFailure();

    public void configureSuccessfullVersion(Environment e) throws ConfigurationFailedException;
    public boolean hasUntriedVersion();
}
