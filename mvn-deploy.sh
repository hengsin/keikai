rid=zk.maven.repo
gid=io.keikai
parentgid=io.keikai.parent
version=5.0.0.1.zk10
keikaipath=keikai/target
kkmodelpath=kkmodel/target

mvn deploy:deploy-file -Dfile=$keikaipath/keikai-oss-5.0.0.1.zk10.jar -DrepositoryId=$rid -Durl=$1 \
	-DgroupId=$gid -DartifactId=keikai-oss \
	-Dsources=$keikaipath/keikai-oss-5.0.0.1.zk10-sources.jar -Dversion=$version

mvn deploy:deploy-file -Dfile=$kkmodelpath/keikai-model-oss-5.0.0.1.zk10.jar -DrepositoryId=$rid -Durl=$1 \
	-DgroupId=$gid -DartifactId=keikai-model-oss \
	-Dsources=$kkmodelpath/keikai-model-oss-5.0.0.1.zk10-sources.jar -Dversion=$version
	
mvn deploy:deploy-file -Dfile=pom.xml -DrepositoryId=$rid -Durl=$1 \
	-DgroupId=$parentgid -DartifactId=keikai-build-oss 	-Dversion=$version
