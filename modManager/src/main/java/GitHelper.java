import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

public class GitHelper {

    private final String repoPath;
    private final String githubUsername;
    private final String githubToken;

    public GitHelper(String repoPath, String githubUsername, String githubToken) {
        this.repoPath = repoPath;
        this.githubUsername = githubUsername;
        this.githubToken = githubToken;
    }

    public void commitAndPush(String filePath, String commitMessage) {
        try {
            // Open the repository
            Git git = Git.open(new File(repoPath));

            // Add the specified file to the git index
            git.add().addFilepattern(filePath).call();

            // Commit the changes with the provided message
            git.commit().setMessage(commitMessage).call();

            // Push to GitHub
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(githubUsername, githubToken))
                    .call();

            System.out.println("Changes committed and pushed to GitHub.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
