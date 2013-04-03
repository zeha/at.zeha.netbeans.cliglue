package at.zeha.netbeans.cliglue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ProjectConfiguration;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Env;
import org.netbeans.spi.sendopts.OptionProcessor;
import org.netbeans.spi.sendopts.Option;
import org.openide.LifecycleManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

@ServiceProvider(service = OptionProcessor.class)
public class AllConfigurationsOpenerCommandLineProcessor extends OptionProcessor {

    private static Option action = Option.requiredArgument(Option.NO_SHORT_NAME, "openallconfigs");
    private static final Logger logger = Logger.getLogger(AllConfigurationsOpenerCommandLineProcessor.class.getName());

    @Override
    public Set<org.netbeans.spi.sendopts.Option> getOptions() {
        return Collections.singleton(action);
    }

    @Override
    protected void process(Env env, Map<Option, String[]> values) throws CommandException {
        final String[] args = values.get(action);
        if (args.length > 0) {
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                @Override
                public void run() {
                    try {
                        ProjectManager manager = ProjectManager.getDefault();
                        manager.clearNonProjectCache();
                        String projectFilePath = args[0];
                        File projectFile = new File(projectFilePath);
                        FileObject projectToBeOpened = FileUtil.toFileObject(projectFile);
                        Project project = ProjectManager.getDefault().findProject(projectToBeOpened);
                        Project[] array = new Project[1];
                        array[0] = project;

                        OpenProjects OpenProjectsInstance = OpenProjects.getDefault();

                        Project[] currentOpenProjects = OpenProjectsInstance.openProjects().get();

                        logger.log(Level.ALL, "Closing existing projects...");
                        OpenProjectsInstance.close(currentOpenProjects);
                        // wait for close to complete
                        currentOpenProjects = OpenProjectsInstance.openProjects().get();

                        logger.log(Level.ALL, "Opening project...");
                        OpenProjectsInstance.open(array, false);
                        // wait for open to complete
                        currentOpenProjects = OpenProjectsInstance.openProjects().get();

                        project = currentOpenProjects[0];
                        ProjectConfigurationProvider configProvider = project.getLookup().lookup(ProjectConfigurationProvider.class);
                        Collection<ProjectConfiguration> configurations = configProvider.getConfigurations();
                        for (Object c : configurations.toArray()) {
                            logger.log(Level.ALL, "Activating configuration \"" + ((ProjectConfiguration) c).getDisplayName() + "\" ...");
                            configProvider.setActiveConfiguration((ProjectConfiguration) c);
                        }

                        logger.log(Level.ALL, "Closing opened projects...");
                        OpenProjectsInstance.close(currentOpenProjects);
                        // wait for close to complete
                        currentOpenProjects = OpenProjectsInstance.openProjects().get();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        logger.log(Level.ALL, ex.getStackTrace().toString());
                    } catch (ExecutionException ex) {
                        ex.printStackTrace();
                        logger.log(Level.ALL, ex.getStackTrace().toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        logger.log(Level.ALL, ex.getStackTrace().toString());
                    }

                    logger.log(Level.ALL, "Shutting down.");
                    LifecycleManager.getDefault().exit();
                }
            });
        }
    }
}
