def pomFile = new File( basedir, 'pom.xml' )
System.out.println( "Slurping POM: ${pomFile.getAbsolutePath()}" )


File buildLog = new File( basedir, 'build.log' )
assert buildLog.getText().contains( "Activating GrabDependencyPopulator extension" )
assert buildLog.getText().contains( "Adding to project the dependencies" )
