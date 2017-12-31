package cool.graph.singleserver

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cool.graph.api.ApiDependencies
import cool.graph.api.database.Databases
import cool.graph.api.project.{ProjectFetcher, ProjectFetcherImpl}
import cool.graph.api.schema.SchemaBuilder
import cool.graph.deploy.DeployDependencies
import cool.graph.deploy.migration.migrator.{AsyncMigrator, Migrator}
import cool.graph.messagebus.pubsub.inmemory.InMemoryAkkaPubSub

trait SingleServerApiDependencies extends DeployDependencies with ApiDependencies {
  override implicit def self: SingleServerDependencies
}

case class SingleServerDependencies(sssEventsPubSub: InMemoryAkkaPubSub[String])(implicit val system: ActorSystem, val materializer: ActorMaterializer)
    extends SingleServerApiDependencies {
  override implicit def self = this

  val databases                      = Databases.initialize(config)
  val apiSchemaBuilder               = SchemaBuilder()
  val projectFetcher: ProjectFetcher = ProjectFetcherImpl(Vector.empty, config)
  val migrator: Migrator             = AsyncMigrator(clientDb, migrationPersistence, projectPersistence, migrationApplier)
}
