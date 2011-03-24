class CreateFalls < ActiveRecord::Migration
  def self.up
    create_table :falls do |t|
      t.text :details
      t.timestamps
    end
  end

  def self.down
    drop_table :falls
  end
end
