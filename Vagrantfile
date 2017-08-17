# -*- mode: ruby -*-
# vi: set ft=ruby :

require "fileutils"

Vagrant.configure("2") do |config|

  cluster_nodes = 3
  zookeeper_default_port = 2181
  kafka_default_port = 9092
  kafka_replication_port = 9192
  cluster_info = {}

  (1..cluster_nodes).each do |i|
    cluster_info["kafka#{i}"] = {
        :id => i,
        :ip => "192.168.56.1#{i-1}"
    }
  end

  cluster_info.each_with_index do |(node_name, node_info), idx|

    config.vm.define node_name, autostart: true do |node|
      node.vm.box = "centos/7"
      node.ssh.pty = true
      node.vm.hostname = node_name
      node.vm.network :private_network, :ip => node_info[:ip]
      node.vm.provision :hosts, :sync_hosts => true
      node.vm.provider :virtualbox do |vb|
        vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
      end

      if idx == (cluster_info.size - 1)
        node.vm.provision :ansible do |ansible|
          ansible.verbose = "vv"
          ansible.playbook = "ansible/playbook.yml"
          ansible.sudo = true
          ansible.limit = 'all'
          ansible.groups = {
              "nodes" => cluster_info.keys
          }
          ansible.extra_vars = {
              cluster_info: cluster_info,
              zookeeper_port: zookeeper_default_port,
              kafka_port: kafka_default_port,
              kafka_replication_port: kafka_replication_port,
              ca_certs_folder: '/tmp/ca-certs'
          }
        end
      end

    end

  end

end
