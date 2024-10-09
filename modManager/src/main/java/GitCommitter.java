import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GitCommitter {

    private String repoPath;
    private String githubToken;
    private String githubUsername;
    private String repository;

    public GitCommitter(String repoPath, String tokenFilePath, String githubUsername, String repository) throws IOException {
        this.repoPath = repoPath;
        this.githubUsername = githubUsername;
        this.repository = repository;

        // Load GitHub token from an external file
        this.githubToken = new String(Files.readAllBytes(Paths.get(tokenFilePath))).trim();
    }

    public void commitAndPush(String commitMessage) throws IOException, InterruptedException {
        // Set the remote URL with the GitHub token
        setRemoteWithToken();

        // Stage the changes (git add .)
        runCommand("git add .");

        // Commit the changes with the provided commit message
        runCommand("git commit -m \"" + commitMessage + "\"");

        // Push the changes to GitHub
        runCommand("git push origin main");
    }

    private void setRemoteWithToken() throws IOException, InterruptedException {
        // Construct the GitHub remote URL with the token
        String remoteUrl = "https://"
                + githubToken
                + "@github.com/"
                + githubUsername
                + "/"
                + repository
                + ".git";

        // Set the remote URL in the repository
        runCommand("git remote set-url origin " + remoteUrl);
    }

    private void runCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new java.io.File(repoPath));
        processBuilder.command("bash", "-c", command);

        Process process = processBuilder.start();
        process.waitFor();
    }
}
