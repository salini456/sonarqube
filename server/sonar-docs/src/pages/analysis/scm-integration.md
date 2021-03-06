---
title: SCM Integration
url: /analysis/scm-integration/
---

Collecting SCM data during code analysis can unlock a number of SonarQube features:

* Automatic Issue Assignment
* code annotation (blame data) in the Code Viewer
* SCM-driven detection of new code (to help with [Clean as You Code](/user-guide/clean-as-you-code/)). Without SCM data, SonarQube determines new code using analysis dates (to timestamp modification of lines).

SCM integration requires support for your individual SCM provider. Git and SVN are supported by default. For other SCM providers, see the Marketplace.

If need be, you can toggle it off at global level via administration settings and at a project level via project settings.

## Git
[Git](http://www.git-scm.com/) integration is supported out of the box with a pure Java implementation so there's no need to have Git command line tool installed on the machine where analysis is performed.

Auto-detection of Git during analysis will happen if there is a .git folder in the project root directory or in one of its parent folders. Otherwise you can force the provider using `-Dsonar.scm.provider=git`. A full clone is required for this integration to be able to collect the required blame information (see Known Issues). If a shallow clone is detected, a warning will be logged and no attempt will be made to retrieve blame information.

Git integration uses [JGit](https://www.eclipse.org/jgit/). JGit is a pure Java implementation of the Git client.

### Known Issues

* Git doesn't consider old "Mac" line ends (CR) as new lines. As a result the blame operation will contain fewer lines than expected by SonarQube and analysis will fail. The solution is to fix line ends to use either Windows (CR/LF) or Unix (LF) line ends.
* JGit doesn't support .mailmap file to "clean" email adress during the blame
* "Missing blame information..." and "Could not find ref..." can be caused by checking out with a partial / shallow clone, or using Git submodules.

### How to investigate error during blame (only possible on Unix/Linux)?

If you get an error when blame is executed on a file, it may be a limitation or a bug in JGit. To confirm please follow these steps:

1. Download the standalone JGit command line distribution

2. Try to execute the blame command on the offending file:  
    `chmod +x /path/to/org.eclipse.jgit.pgm-4.9.0.201710071750-r.sh /path/to/org.eclipse.jgit.pgm-4.9.0.201710071750-r.sh blame -w /path/to/offending/file`

3. If you get the same error as during analysis, then this really looks like a bug in JGit (especially if you don't have an issue with the native git command line tool). Please try to do the previous steps with latest version of JGit and report all information to the [SonarQube Community Forum](https://community.sonarsource.com/).

## Subversion
[Subversion](https://subversion.apache.org/) integration is supported out of the box for Subversion 1.6 to 1.9.x.

Auto-detection of SVN during analysis will happen if there is a `.svn` folder somewhere in the parent hierarchy of the project root. Otherwise you can force the provider using `-Dsonar.scm.provider=svn` on the analysis command line.

### Authentication
In order to get blame information on your code you will need to supply authentication data to the scanner. You can do it by passing following parameters to it when starting an analysis:

| Parameter Name        | Description |
| --------------------- | ---------------------------------- |
| `sonar.svn.username` | Username to be used for SVN server or SVN+SSH authentication |
| `sonar.svn.password.secured` | Password to be used for SVN server or SVN+SSH authentication |
| `sonar.svn.privateKeyPath` | Path to private key file. Can be used instead of password for SVN+SSH authentication |
| `sonar.svn.passphrase.secured` | Optional passphrase of your private key file |

### Known issues
If you get errors like:

`Caused by: org.tmatesoft.svn.core.SVNException: svn: E200007: Retrieval of mergeinfo unsupported by 'https://pmd.svn.sourceforge.net/svnroot/pmd/trunk/pmd/src/main/java/net/sourceforge/pmd/AbstractConfiguration.java';`
It means the SVN server is not advertising the 'mergeinfo' capability. You can check the advertised capabilities by simply connecting to it:

`telnet <svn_server> 3690`
Often this is because your SVN server is not >= 1.5 or your project was not properly migrated after a server upgrade. It could also be a misconfiguration of the server.

You should try to run svnadmin upgrade **on the server**. For more information, please read https://subversion.apache.org/docs/release-notes/1.5.html#repos-upgrades.

