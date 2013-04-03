at.zeha.netbeans.cliglue
========================

Aka "All Configs Opener".

Why?
----

The NetBeans 6.x C/C++ Development environment creates platform-/
machine-specific Makefiles, which you really shouldn't check into version
control.

Not checking these files into your VCS means, you must manually open all
projects, manually open all configured configurations to regenerate these
Makefiles before a command line build will work.

Remedy
------

Install this plugin, run netbeans --openallconfigs /full/path/to/project.

NetBeans will open the specified project, activate all configurations,
and quit.

PS: Also works with MPLAB X.


Download: https://github.com/zeha/at.zeha.netbeans.cliglue/raw/dist/at-zeha-netbeans-cliglue.nbm


  -- Christian Hofstaedtler <ch@zeha.at>
