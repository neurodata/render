mkdir -p $VOLUMEMOUNTPOINT/render-ws/target
mkdir -p $VOLUMEMOUNTPOINT/render-app/target
mkdir -p $VOLUMEMOUNTPOINT/render-ws-java-client/target
mkdir -p $VOLUMEMOUNTPOINT/render-ws-spark-client/target
cp render-ws/target/*.war $VOLUMEMOUNTPOINT/render-ws/target/.
cp render-app/target/*.jar $VOLUMEMOUNTPOINT/render-app/target/.
cp render-ws-java-client/target/*.jar $VOLUMEMOUNTPOINT/render-ws-java-client/target/.
cp render-ws-spark-client/target/*.ar $VOLUMEMOUNTPOINT/render-ws-spark-client/target/.
