package com.intlimit.grsplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.lsp.api.LspCommunicationChannel;
import com.intellij.platform.lsp.api.LspServerSupportProvider;
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.GroovyFileType;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class GrSServerSupportProvider implements LspServerSupportProvider {
    @Override
    public void fileOpened(@NotNull Project project, @NotNull VirtualFile virtualFile, @NotNull LspServerSupportProvider.LspServerStarter lspServerStarter) {
        if (virtualFile.getFileType().getName().equals(GroovyFileType.GROOVY_FILE_TYPE.getName()) &&
                Objects.requireNonNull(GrSSettings.getInstance(project).getState()).enable) {
            lspServerStarter.ensureServerStarted(new GrSServerDescriptor(project));
        }
    }

    private static class GrSServerDescriptor extends ProjectWideLspServerDescriptor {

        private final LspCommunicationChannel channel;

        public GrSServerDescriptor(@NotNull Project project) {
            super(project, "GroovyScript");

            channel = new LspCommunicationChannel.Socket(
                    Objects.requireNonNull(GrSSettings.getInstance(project).getState()).port, false);
        }

        @Override
        public @NotNull LspCommunicationChannel getLspCommunicationChannel() {
            return channel;
        }

        @Override
        public boolean isSupportedFile(@NotNull VirtualFile virtualFile) {
            return virtualFile.getFileType().getName().equals(GroovyFileType.GROOVY_FILE_TYPE.getName());
        }
    }
}
