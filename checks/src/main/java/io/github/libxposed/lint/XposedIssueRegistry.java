package io.github.libxposed.lint;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class XposedIssueRegistry extends IssueRegistry {

    @Override
    public @NotNull List<Issue> getIssues() {
        return List.of();
    }
}
